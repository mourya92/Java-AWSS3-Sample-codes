import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.amazonaws.services.s3.transfer.Upload;

/**
 * 
 */

/**
 * @author ssmourya
 *
 */
public class UploadObj {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BasicAWSCredentials creds = new BasicAWSCredentials("",
				"");
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				//.withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com", "us-east-1"))
				.withPayloadSigningEnabled(false).withChunkedEncodingDisabled(true).build();

		String srcbucket = "";
		String srckey = "Reverse_Employee.py";
		String srckey2 = "bigfile";
		String destbucket = "";

		TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
		// TransferManager xfer_mgr =
		// TransferManagerBuilder.standard().withMultipartCopyThreshold((long)
		// 769331000).withMultipartCopyPartSize((long)
		// 104857600).withS3Client(s3).build();
		try {

			ObjectMetadata metadata = new ObjectMetadata();

			List<Tag> newTags = new ArrayList<Tag>();
			newTags.add(new Tag("Tag", "This is tag"));

			Upload xfer = xfer_mgr.upload(new PutObjectRequest(srcbucket, "helloworld.dmg",
					new File("/Users/ssmourya/Downloads/android-studio-ide-171.4443003-mac.dmg"))
							.withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams("57d56310-ecb3-4401-ad77-d8764a5b626f"))
							.withTagging(new ObjectTagging(newTags)));

			System.out.println("Waiting for Upload completion");
			xfer.waitForCompletion();
			// print the final state of the transfer.
			TransferState xfer_state = xfer.getState();
			System.out.println("Upload : " + xfer_state);

			/*
			 * xfer_mgr.shutdownNow();
			 * 
			 * 
			 * xfer = xfer_mgr.upload(new PutObjectRequest(srcbucket, srckey2,
			 * new File(
			 * "/Users/ssmourya/Downloads/android-studio-ide-171.4443003-mac.dmg"
			 * )).withTagging(new ObjectTagging(newTags)));
			 * 
			 * System.out.println("Waiting for Upload completion");
			 * xfer.waitForCompletion(); // print the final state of the
			 * transfer. xfer_state = xfer.getState();
			 * System.out.println("Upload : " + xfer_state);
			 */

		} catch (AmazonServiceException e) {
			System.err.println(e);
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
