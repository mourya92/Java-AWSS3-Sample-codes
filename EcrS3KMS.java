import java.io.File;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SSECustomerKey;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

public class EcrS3KMS {

    public static void main(String[] args) throws Exception {
        String existingBucketName = "";
        String fileToUpload       = "/Users/ssmourya/Documents/AWSS3/Python-SDK/test";
        String keyName            = "uploadEncr";
        String targetKeyName      = "uploadEncr2";
        
        BasicAWSCredentials creds = new BasicAWSCredentials("", "");
		
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))	
				//.withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com/", "us-east-1"))
				.build();
		
		TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3).build();  
        
        // 1. first create an object from a file. 
        PutObjectRequest putObjectRequest = new PutObjectRequest(existingBucketName, keyName, new File(fileToUpload));
        putObjectRequest.setCannedAcl(CannedAccessControlList.BucketOwnerFullControl);
        
        // we want object stored using SSE-C. So we create encryption key.
        SecretKey secretKey1 = generateSecretKey();
        SSECustomerKey sseCustomerEncryptionKey1 = new SSECustomerKey(secretKey1);
        
        putObjectRequest.setSSECustomerKey(sseCustomerEncryptionKey1);
        // now create object.
        //Upload upload = tm.upload(existingBucketName, keyName, new File(sourceFile));
        Upload upload = tm.upload(putObjectRequest);
        try {
        	// Or you can block and wait for the upload to finish
        	upload.waitForCompletion();
        	//tm.getAmazonS3Client().putObject(putObjectRequest);
        	System.out.println("Object created.");
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }

        // 2. Now make object copy (in the same bucket). Store target using sse-c.
       
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(existingBucketName, keyName, existingBucketName, targetKeyName);
        copyObjectRequest.setCannedAccessControlList(CannedAccessControlList.BucketOwnerFullControl);
        
        SecretKey secretKey2 = generateSecretKey();
        SSECustomerKey sseTargetObjectEncryptionKey = new SSECustomerKey(secretKey2);
        
        
        copyObjectRequest.setSourceSSECustomerKey(sseCustomerEncryptionKey1);
        copyObjectRequest.setDestinationSSECustomerKey(sseTargetObjectEncryptionKey);
        
     // Get the existing object ACL that we want to modify.
        

        
        // Grant a sample set of permissions, using the existing ACL owner for Full Control permissions.
        
        
        // TransferManager processes all transfers asynchronously, 
        // so this call will return immediately.
        Copy copy = tm.copy(copyObjectRequest);
        try {
        	// Or you can block and wait for the upload to finish
        	copy.waitForCompletion();
        	System.out.println("Copy complete.");
        } catch (AmazonClientException amazonClientException) {
        	System.out.println("Unable to upload file, upload was aborted.");
        	amazonClientException.printStackTrace();
        }
    }
    
    private static SecretKey generateSecretKey() {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(256, new SecureRandom());
            return generator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
    
}
