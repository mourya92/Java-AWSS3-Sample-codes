import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class Presigned {
	private static String bucketName = ""; 
	private static String objectKey  =  "test";
	
	public static void main(String[] args) throws IOException {

		BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", "");
		
		//InstanceProfileCredentialsProvider pc = new InstanceProfileCredentialsProvider(false);
        //BasicSessionCredentials creds_sess = (BasicSessionCredentials) pc.getCredentials();

        //System.out.println(creds_sess.getSessionToken());
        //System.out.println(creds_sess.getAWSAccessKeyId());
        //System.out.println(creds_sess.getAWSSecretKey());
        
        
		
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
        		.withPathStyleAccessEnabled(true)
        		.withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com", "us-east-1"))
        		.withCredentials(new AWSStaticCredentialsProvider(creds)).build();	
	
			
		try {
			System.out.println("Generating S3 pre-signed URL.");
			
			
			java.util.Date expiration = new java.util.Date();
			long milliSeconds = expiration.getTime();
			milliSeconds += 1000 * 60 * 60; // Add 1 hour.
			expiration.setTime(milliSeconds);
			
			
			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				    new GeneratePresignedUrlRequest(bucketName, objectKey);
			generatePresignedUrlRequest.setMethod(HttpMethod.PUT); 
			generatePresignedUrlRequest.setExpiration(expiration);

			URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest); 

			System.out.println(url.toString());
			
			/*
			 * code to make the HTTP Request
			 */
			
			String bucketName = "";
			String stringObjKeyName = "test-meta-data-object";
			String fileName = "/Users/ssmourya/Documents/workspace/PresignedURL/src/samplefile";
			
			// Upload a text string as a new object.
			s3client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");
			System.out.println("Uploaded");
			
            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, stringObjKeyName, new File(fileName));
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.setContentLength(200);
            metadata.addUserMetadata("title", "groß.jpg");
            request.setMetadata(metadata);
            
            s3client.putObject(request);
            System.out.println("Uploaded");

		} catch (AmazonServiceException exception) {
			System.out.println("Caught an AmazonServiceException, " +
					"which means your request made it " +
					"to Amazon S3, but was rejected with an error response " +
			"for some reason.");
			System.out.println("Error Message: " + exception.getMessage());
			System.out.println("HTTP  Code: "    + exception.getStatusCode());
			System.out.println("AWS Error Code:" + exception.getErrorCode());
			System.out.println("Error Type:    " + exception.getErrorType());
			System.out.println("Request ID:    " + exception.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, " +
					"which means the client encountered " +
					"an internal error while trying to communicate" +
					" with S3, " +
			"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}
