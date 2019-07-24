import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.glacier.transfer.UploadResult;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.AmazonGlacier;

public class UploadArchive {
    public static String vaultName = "TestGlacierVault";
    public static String archiveToUpload = "/Users/ssmourya/Downloads/temp_10GB_file.1cdBa63d";
    
    public static void main(String[] args) throws IOException {
        
        
    	 BasicAWSCredentials creds = new BasicAWSCredentials("AKIA", "");
    	
    	AmazonGlacier client = AmazonGlacierClientBuilder.standard()
    			.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				.build();
    	
        try {
        	ArchiveTransferManager atm = new ArchiveTransferManagerBuilder().withGlacierClient(client).build();
        	
            UploadResult result = atm.upload(vaultName, "my archive " + (new Date()), new File(archiveToUpload));
            System.out.println("Archive ID: " + result.getArchiveId());
            
        } catch (Exception e)
        {
            System.err.println(e);
        }
    }
}
