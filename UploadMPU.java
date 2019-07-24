
/**
 * @author ssmourya
 * 
 * The Below code uploads a file from local machine to Amazon S3 Bucket. It uses Multipart chunks of size 5 MB each. 
 * This code also makes use of Amazon S3 ClientSide Encryption. 
 * This code uses a Modified version of AWS Java SDK 1.11.326. The modification I did is to add Content-MD5 Header value of the Encrypted Data. 
 *
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.internal.ResettableInputStream;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

public class UploadMPU {

    public static void main(String[] args) throws IOException {
    	
    	
        String clientRegion = "us-east-1";
        String bucketName = "";
        String keyName = "android-studio-ide-171.4443003-mac.dmg";
        String filePath = "/Users/ssmourya/Downloads/android-studio-ide-171.4443003-mac.dmg";
        
        
        String kms_cmk_id = null;
        
        File file = new File(filePath);
        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB.
        long maxpartSize = 5 * 1024 * 1024;
        
        try {
        	BasicAWSCredentials creds = new BasicAWSCredentials("AKI", ""); 
            
        	
        	AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(creds))
                    .withRegion(clientRegion)
                    .build();
        	
        	CreateKeyResult keyResult = kmsClient.createKey();
        	kms_cmk_id = keyResult.getKeyMetadata().getKeyId();

        	System.out.println(kms_cmk_id);
        	
        	// Create the encryption client.
        	KMSEncryptionMaterialsProvider materialProvider = new KMSEncryptionMaterialsProvider(kms_cmk_id);
        	CryptoConfiguration cryptoConfig = new CryptoConfiguration()
        			.withAwsKmsRegion(RegionUtils.getRegion(clientRegion));

        	 AmazonS3Encryption s3Client = AmazonS3EncryptionClientBuilder.standard()
                     .withCredentials(new AWSStaticCredentialsProvider(creds))
                     .withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com/", "us-east-1")) //setting this endpoint to HTTP to capture the wire logs for troubleshooting
                     .withEncryptionMaterials(materialProvider)
                     .withCryptoConfiguration(cryptoConfig)
                     //.withRegion(clientRegion) // Uncomment this line if you want to use HTTPS and comment out EndpointConfiguration setting
                     .build();
        	 
        	/* 
        	// Uncomment this code if you want to use Standard S3 Client Object i.e., No Amazon S3 ClientSide Encryption. 
        	 
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                    .withCredentials(new AWSStaticCredentialsProvider(creds))
                                    .withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com/", "us-east-1"))
                                    .build();
        	 */
        	
            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to 
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<PartETag>();

            // Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
            InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
            
            
            // Upload the file parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = (int) Math.min(partSize, (contentLength - filePosition));
                
                System.out.println(partSize);
                // Create the request to upload a part.
                UploadPartRequest uploadRequest = null;
                
                if (partSize < maxpartSize) {
                	/**
                	* If part size is more than the file content length it
                	* should be set as the last part.
                	*/
                	// Create request to upload last part.
                	uploadRequest = new UploadPartRequest()
	                	.withBucketName(bucketName)
	                	.withKey(keyName)
	                	.withUploadId(initResponse.getUploadId())
	                	.withPartNumber(i)
	                	.withFileOffset(filePosition)
	                	.withFile(file)
	                	.withLastPart(true)
	                	.withPartSize(partSize); // Do not set Content-MD5 header because its already set in SDK 
                	} else {
                	// Create request to upload a part.
                	uploadRequest = new UploadPartRequest()
	                	.withBucketName(bucketName)
	                	.withKey(keyName)
	                	.withUploadId(initResponse.getUploadId())
	                	.withPartNumber(i)
	                	.withFileOffset(filePosition)
	                	.withFile(file)
	                	.withPartSize(partSize); // Do not set Content-MD5 header because its already set in SDK
                	}
                
                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                                       
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName,
                    initResponse.getUploadId(), partETags);
            System.out.println("Completed Multipart Upload");
            
            s3Client.completeMultipartUpload(compRequest);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
