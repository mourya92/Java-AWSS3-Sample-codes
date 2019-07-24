
import java.io.IOException;
import java.util.Arrays;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Transition;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleAndOperator;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;


public class LifecycleConfiguration {

	public static String bucketName = "CrossAccntTest";
    public static AmazonS3Client s3Client;

    public static void main(String[] args) throws IOException {
        s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {

            BucketLifecycleConfiguration.Rule rule =
                    new BucketLifecycleConfiguration.Rule()
                            .withId("Delete after use")
                            .withFilter(new LifecycleFilter(
                                    new LifecyclePrefixPredicate(""))) // Explicitly set the Prefix to "" (Whole Bucket)
                            .withExpirationInDays(1)
                            .withStatus(BucketLifecycleConfiguration.ENABLED.toString());
            

         // Set the bucket life cycle configuration
            BucketLifecycleConfiguration configuration = s3Client.getBucketLifecycleConfiguration(bucketName);
            
            if (configuration == null) {
            	
                configuration = new BucketLifecycleConfiguration().withRules(rule);
                s3Client.setBucketLifecycleConfiguration(bucketName, configuration);
                System.out.println("Configuration set.");
            } else {
                configuration.getRules().add(rule);
                System.out.println("Configuration found.");
            }
            

        } catch (AmazonS3Exception amazonS3Exception) {
            System.out.format("An Amazon S3 error occurred. Exception: %s", amazonS3Exception.toString());
        } catch (Exception ex) {
            System.out.format("Exception: %s", ex.toString());
        }
    }

}
