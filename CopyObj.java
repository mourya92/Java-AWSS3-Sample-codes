import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3control.AWSS3Control;
import com.amazonaws.services.s3control.AWSS3ControlClientBuilder;
import com.amazonaws.services.s3control.model.GetPublicAccessBlockRequest;

/**
 * 
 */

/**
 * @author ssmourya
 *
 */
public class CopyObj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicAWSCredentials creds = new BasicAWSCredentials("A", "");
		
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				//.withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com/", "us-east-1"))
				.build();

		String srcbucket = "";
		String srckey = "largefile";
		String srckey2 = "largefile3";
		String destbucket = "";

		GetObjectMetadataRequest meta = new GetObjectMetadataRequest("", "werid/useast1+credit-card.market-decide.account-setup.fulfillment-data.v3+7+0000000770.avro");
		
		ObjectMetadata objectMetadata = s3.getObjectMetadata(meta);
		
		System.out.println(objectMetadata.getETag());
				
				//TransferManagerConfiguration tmc = new TransferManagerConfiguration();
		//tmc.setMultipartCopyThreshold(769331000);
		//tmc.setMultipartCopyPartSize(104857600);
	
		TransferManager xfer_mgr = TransferManagerBuilder.standard()
				.withMultipartCopyThreshold((long) 769331000)
			    .withMultipartCopyPartSize((long) 104857600)
				.withS3Client(s3).build();
		
		System.out.println(xfer_mgr.getConfiguration().getMultipartCopyThreshold());
		System.out.println(xfer_mgr.getConfiguration().getMultipartUploadThreshold());
		System.out.println(xfer_mgr.getConfiguration().getMultipartCopyPartSize());
		System.out.println(xfer_mgr.getConfiguration().getMinimumUploadPartSize());
		
		
	
		
		try {
			
			
			
			AWSS3ControlClientBuilder controlClientBuilder = AWSS3ControlClientBuilder.standard();
			controlClientBuilder.setRegion("us-east-2");
			controlClientBuilder.setCredentials(new AWSStaticCredentialsProvider(creds));
								
			AWSS3Control client = controlClientBuilder.build();
			System.out.println(client.getPublicAccessBlock(new GetPublicAccessBlockRequest().withAccountId("460139450784")));
					
			
			
			//Copy xfer = xfer_mgr.copy(srcbucket, srckey2, destbucket, srckey2);

			// Forming a COPY Request
			CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcbucket, srckey, srcbucket, srckey2);
			
			//Get the Tags for source object
			GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(srcbucket, srckey);
			GetObjectTaggingResult getTagsResult = s3.getObjectTagging(getTaggingRequest);

			//Add Tag to the COPY Request ; Same as Tags for source object
			
			List<Tag> tags = new ArrayList<Tag>();
            tags.add(new Tag("Tag 1", "This is tag 1"));
            tags.add(new Tag("Tag 2", "This is tag 2"));
            
			//ObjectTagging objectTagging = new ObjectTagging(getTagsResult.getTagSet());
			ObjectTagging objectTagging = new ObjectTagging(tags);
			copyObjectRequest.setNewObjectTagging(objectTagging);
			copyObjectRequest.setCannedAccessControlList(CannedAccessControlList.BucketOwnerFullControl);
			
			System.out.println(copyObjectRequest.getNewObjectTagging());
			//Start the COPY operation
			Copy xfer = xfer_mgr.copy(copyObjectRequest);
			System.out.println("Waiting for copy completion");
			xfer.waitForCompletion();
			
			// Print the final state of the transfer.
			TransferState xfer_state = xfer.getState();
			System.out.println("Copy : " + xfer_state);
			
			//Set Destination Object Tags
			//s3.setObjectTagging(new SetObjectTaggingRequest(destbucket, srckey2, objectTagging));
			
			System.out.println("Done");

		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
