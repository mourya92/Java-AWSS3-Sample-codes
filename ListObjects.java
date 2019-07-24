import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ResourceBundle;

public class ListObjects {

	public static int printObjectListBatch(ListObjectsV2Result v2Res_lor){
		
		System.out.println(" =============================================== ");
		System.out.println("Total Keys in Current List: ");
		System.out.println(v2Res_lor.getKeyCount());
		
		
    	System.out.println("Key List: ");
    	for (S3ObjectSummary ObjSumm : v2Res_lor.getObjectSummaries()) {
			System.out.println("Key Name: " + ObjSumm.getKey());
			System.out.println("LastModified Time: " + ObjSumm.getLastModified().);
		}
		 
		
		System.out.println(" =============================================== ");
		System.out.println("Current Continuation Token: ");
		System.out.println(v2Res_lor.getContinuationToken());
		
		System.out.println(" =============================================== ");
    	System.out.println("Next Continuation Token: ");
		System.out.println(v2Res_lor.getNextContinuationToken());
		
		return v2Res_lor.getKeyCount();
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		private Date dateTime;
		
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", ""); 
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		.withRegion(Regions.US_EAST_1)
        		.withCredentials(new AWSStaticCredentialsProvider(creds)).build();
        
        try {

            String nextToken = null; 
            int totalKeys = 0; 
            
            System.out.println("Listing Objects");
            
            ListObjectsV2Request v2Req_lor = new ListObjectsV2Request();
            
            v2Req_lor.setBucketName("");        
            
            ListObjectsV2Result v2Res_lor = s3.listObjectsV2(v2Req_lor);          
            
            nextToken = v2Res_lor.getNextContinuationToken();
            		
            while (null != nextToken) {      	
            	
            	totalKeys += printObjectListBatch(v2Res_lor); 
            	
        		System.out.println(" =============================================== ");				
            	System.out.println("Sum of Total Keys: " + totalKeys);    	
            	
            	v2Req_lor.setContinuationToken(nextToken);
            	
            	v2Res_lor = s3.listObjectsV2(v2Req_lor);
             	
             	nextToken = v2Res_lor.getNextContinuationToken();
            	
			} 
            
            totalKeys += printObjectListBatch(v2Res_lor); 
        	
    		System.out.println(" =============================================== ");				
        	System.out.println("Sum of Total Keys: " + totalKeys);
			
			System.out.println();

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        
        

	}

}
