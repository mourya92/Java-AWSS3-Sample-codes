import com.amazonaws.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesRequest;
import com.amazonaws.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesResult;
import com.amazonaws.services.cloudfront.model.ListDistributionsRequest;
import com.amazonaws.services.cloudfront.model.ListDistributionsResult;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CloudFrontOriginAccessIdentityList;
import com.amazonaws.services.cloudfront.model.CloudFrontOriginAccessIdentitySummary;
import com.amazonaws.services.cloudfront.model.DistributionList;
import com.amazonaws.services.cloudfront.model.DistributionSummary;

public class UpdateDistro {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		 BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", "");
		
		 AmazonCloudFront cf = AmazonCloudFrontClientBuilder.standard()
				 				.withCredentials(new AWSStaticCredentialsProvider(creds))
				 				.build();
		 
		 ListCloudFrontOriginAccessIdentitiesResult oais = cf.listCloudFrontOriginAccessIdentities(new ListCloudFrontOriginAccessIdentitiesRequest());
		 
		 CloudFrontOriginAccessIdentityList oai_list = oais.getCloudFrontOriginAccessIdentityList();
		 
		 System.out.println("=========================================");
		 System.out.println("* List of OAIs in your Account :");
		 for (CloudFrontOriginAccessIdentitySummary each_oai : oai_list.getItems()){
			 System.out.println("* OAI ID : " + each_oai.getId() + "| " + "Canonical ID: " + each_oai.getS3CanonicalUserId());
		 }
		 
		 ListDistributionsResult distros = cf.listDistributions(new ListDistributionsRequest());
		 DistributionList distroList = distros.getDistributionList();
		 
		 System.out.println("=========================================");
		 System.out.println("* Distribution ID which contains S3 Origins that are associated with an OAI :");
		 
		 for ( DistributionSummary each_distro : distroList.getItems()){
			 
			 System.out.println("=========================================");
			 System.out.println("* Distribution ID : " + each_distro.getId());
			 
			 
			 for(Origin origin : each_distro.getOrigins().getItems()){
				 
				 if(null != origin.getS3OriginConfig()){
					String idwithoai = origin.getS3OriginConfig().getOriginAccessIdentity();
					if (0 != idwithoai.length()){
						 System.out.println("\t* Origin ID : " + origin.getId());
						 System.out.println("\t* OAI Used  : " + idwithoai);
					}
				 }
				 
			 }
			 
		 }
		
	}

}
