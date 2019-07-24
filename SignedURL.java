import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URLEncoder;
import java.security.Security;
import java.text.ParseException;

import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;


import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

public class SignedURL {

	public static void main(String[] args) throws FileNotFoundException, IOException, CloudFrontServiceException, ParseException {
		// Signed URLs for a private distribution
		// Note that Java only supports SSL certificates in DER format, 
		// so you will need to convert your PEM-formatted file to DER format. 
		// To do this, you can use openssl:
		// openssl pkcs8 -topk8 -nocrypt -in origin.pem -inform PEM -out new.der 
//		    -outform DER 
		// So the encoder works correctly, you should also add the bouncy castle jar
		// to your project and then add the provider.

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String distributionDomain = "bitnami.gudladona92.com";
		String privateKeyFilePath = "/Users/ssmourya/Downloads/pk-APKAIMELWHB5TIG5KCYQ.der";
		String s3ObjectKey = "index.html";
		String keyPairId = "APKAJK6TV2EEQGY7EY4A";
				
		// Convert your DER file into a byte array.

		/*
		ResponseHeaderOverrides override = new ResponseHeaderOverrides();
		String dispositon = "본인확인서비스-이용에-관한-계약_20190225.docx";
		String encodedDisposition = URLEncoder.encode(dispositon, "UTF-8");
		String contentDispositionValue = "attachment; filename*=UTF-8''"+encodedDisposition;
		override.setContentDisposition(contentDispositionValue);
		*/
		
		//System.out.println(override.getContentDisposition());	
		
		byte[] derPrivateKey = ServiceUtils.readInputStreamToBytes(new
		    FileInputStream(privateKeyFilePath));

		// Generate a "canned" signed URL to allow access to a 
		// specific distribution and file

		String signedUrlCanned = CloudFrontService.signUrlCanned(
		    //"https://" + distributionDomain + "/" + s3ObjectKey + "?response-content-disposition=" + override.getContentDisposition() , // Resource URL or Path
			"https://" + distributionDomain + "/" + s3ObjectKey, // Resource URL or Path
		    keyPairId,     // Certificate identifier, 
		                   // an active trusted signer for the distribution
		    derPrivateKey, // DER Private key data
		    ServiceUtils.parseIso8601Date("2019-10-10T22:20:00.000Z") // DateLessThan
		    );
		System.out.println(signedUrlCanned);
		
	}

}
