import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.VersionInfoUtils;

/**
 * 
 */

/**
 * @author ssmourya
 *
 */
public class UploadWeird {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
		
		System.out.println("AWS Java SDK Version :" + VersionInfoUtils.getVersion());
		System.out.println("My platform details :" + VersionInfoUtils.getPlatform());
		System.out.println("User Agent :" + VersionInfoUtils.getUserAgent());
		
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", "");
		
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				//.withRegion(Regions.EU_WEST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))	
				.withEndpointConfiguration(new EndpointConfiguration("http://s3.amazonaws.com/", "us-east-1"))
				.build();

		
		GetObjectMetadataRequest meta = new GetObjectMetadataRequest("", "werid/useast1+credit-card.market-decide.account-setup.fulfillment-data.v3+7+0000000770.avro");
		
		ObjectMetadata objectMetadata = s3.getObjectMetadata(meta);
		
		System.out.println(objectMetadata.getETag());
		
	}

}
