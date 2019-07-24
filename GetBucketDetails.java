import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.text.ParseException;

import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;

public class GetBucketDetails {

	public static void main(String[] args) throws FileNotFoundException, IOException, CloudFrontServiceException, ParseException {
		// TODO Auto-generated method stub
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String distributionDomain = "bitnami.gudladona92.com";
		String privateKeyFilePath = "/Users/ssmourya/Downloads/pk-APKAIMELWHB5TIG5KCYQ.der";
		String s3ObjectKey = "index.html";
		String keyPairId = "APKAJK6TV2EEQGY7EY4A";
				
		// Convert your DER file into a byte array.

		ResponseHeaderOverrides override = new ResponseHeaderOverrides();
		String dispositon = "본인확인서비스-이용에-관한-계약_20190225.docx";
		String encodedDisposition = URLEncoder.encode(dispositon, "UTF-8");
		String contentDispositionValue = "attachment; filename*=UTF-8''"+encodedDisposition;
		override.setContentDisposition(contentDispositionValue);
		
		System.out.println(override.getContentDisposition());	
		
		byte[] derPrivateKey = ServiceUtils.readInputStreamToBytes(new
		    FileInputStream(privateKeyFilePath));

		// Generate a "canned" signed URL to allow access to a 
		// specific distribution and file

		String signedUrlCanned = CloudFrontService.signUrlCanned(
		    "https://" + distributionDomain + "/" + s3ObjectKey + "?response-content-disposition=" + override.getContentDisposition() , // Resource URL or Path
		    keyPairId,     // Certificate identifier, 
		                   // an active trusted signer for the distribution
		    derPrivateKey, // DER Private key data
		    ServiceUtils.parseIso8601Date("2019-03-10T22:20:00.000Z") // DateLessThan
		    );
		System.out.println(signedUrlCanned);
	}

}
