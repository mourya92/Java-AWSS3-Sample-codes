/**
 * 
 */

/**
 * @author ssmourya
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

public class MPUcopy {

    public static void main(String[] args) throws IOException {
        String clientRegion = "us-east-1";
        String sourceBucketName = "SOURCE BUCKET";
        String sourceObjectKey = "SOURCE KEY";
        String destBucketName = "DESTINATION BUCKET";
        String destObjectKey = "DESTINATION KEY";
        
        try {
        	BasicAWSCredentials creds = new BasicAWSCredentials("IAM USER ACCESS KEY", "IAM USER SECRET KEY");
        	
        	AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(creds))
                    .withRegion(clientRegion)
                    .build();
        	
        	GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(sourceBucketName, sourceObjectKey);
			GetObjectTaggingResult getTagsResult = s3Client.getObjectTagging(getTaggingRequest);

			ObjectTagging objectTagging = null;
			objectTagging = new ObjectTagging(getTagsResult.getTagSet());
            
			System.out.println("Source Object Tags : " + getTagsResult.getTagSet().size());
			
			// Initiate the multipart upload.
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(destBucketName, destObjectKey);
            initRequest.withTagging(objectTagging);
            initRequest.withCannedACL(CannedAccessControlList.BucketOwnerFullControl);
            
            InitiateMultipartUploadResult initResult = s3Client.initiateMultipartUpload(initRequest);
    
            // Get the object size to track the end of the copy operation.
            GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(sourceBucketName, sourceObjectKey);
            ObjectMetadata metadataResult = s3Client.getObjectMetadata(metadataRequest);
            long objectSize = metadataResult.getContentLength();
    
            // Copy the object using 5 MB parts.
            long partSize = 5 * 1024 * 1024;
            long bytePosition = 0;
            int partNum = 1;
            List<CopyPartResult> copyResponses = new ArrayList<CopyPartResult>();
            while (bytePosition < objectSize) {
                // The last part might be smaller than partSize, so check to make sure
                // that lastByte isn't beyond the end of the object.
                long lastByte = Math.min(bytePosition + partSize - 1, objectSize - 1);
                
                // Copy this part.
                System.out.println("Uploading Part Number : " + partNum);
                CopyPartRequest copyRequest = new CopyPartRequest()
                        .withSourceBucketName(sourceBucketName)
                        .withSourceKey(sourceObjectKey)
                        .withDestinationBucketName(destBucketName)
                        .withDestinationKey(destObjectKey)
                        .withUploadId(initResult.getUploadId())
                        .withFirstByte(bytePosition)
                        .withLastByte(lastByte)
                        .withPartNumber(partNum++);
                copyResponses.add(s3Client.copyPart(copyRequest));
                bytePosition += partSize;
            }
    
            // Complete the upload request to concatenate all uploaded parts and make the copied object available.
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                                                                        destBucketName,
                                                                        destObjectKey, 
                                                                        initResult.getUploadId(),
                                                                        getETags(copyResponses));
            s3Client.completeMultipartUpload(completeRequest);
            System.out.println("Multipart copy complete.");
            
            getTaggingRequest = new GetObjectTaggingRequest(destBucketName, destObjectKey);
			getTagsResult = s3Client.getObjectTagging(getTaggingRequest);

			objectTagging = null;
			objectTagging = new ObjectTagging(getTagsResult.getTagSet());
            
			System.out.println("Destination Object Tags : " + getTagsResult.getTagSet().size());
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

    // This is a helper function to construct a list of ETags.
    private static List<PartETag> getETags(List<CopyPartResult> responses) {
        List<PartETag> etags = new ArrayList<PartETag>();
        for (CopyPartResult response : responses) {
            etags.add(new PartETag(response.getPartNumber(), response.getETag()));
        }
        return etags;
    }
}

