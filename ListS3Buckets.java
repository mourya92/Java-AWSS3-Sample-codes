/**
 * 
 */
package quickstart;

import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

/**
 * @author ssmourya
 *
 */
public class ListS3Buckets {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", "");
		
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
        		.withPathStyleAccessEnabled(true)
        		.withCredentials(new AWSStaticCredentialsProvider(creds)).build();	
		
		try {
			System.out.println("Listing your S3 Buckets");
			
			List<Bucket> Buckets = s3client.listBuckets(); 
			
			System.out.println("Your Amazon S3 buckets are:");
			for (Bucket b : Buckets) {
			    System.out.println("* " + b.getName());
			}
			
		}catch (AmazonServiceException exception) {
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
