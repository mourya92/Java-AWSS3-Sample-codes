import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.AmazonGlacier;

public class CreateInventory {
	public static String vaultName = "TestGlacierVault";
	public static String archiveToUpload = "/Users/ssmourya/Downloads/temp_10GB_file.1cdBa63d";
	public static AmazonGlacier client = null;
	public static String fileName = "GlacierInventoryResponse.txt";

	private static void downloadJobOutput(String jobId) throws IOException {

		GetJobOutputRequest getJobOutputRequest = new GetJobOutputRequest()
				.withVaultName(vaultName)
				.withJobId(jobId);
		GetJobOutputResult getJobOutputResult = client.getJobOutput(getJobOutputRequest);

		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		BufferedReader in = new BufferedReader(new InputStreamReader(getJobOutputResult.getBody()));            
		String inputLine;
		try {
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				out.write(inputLine);
			}
		}catch(IOException e) {
			throw new AmazonClientException("Unable to save archive", e);
		}finally{
			try {in.close();}  catch (Exception e) {}
			try {out.close();}  catch (Exception e) {}             
		}
		System.out.println("Retrieved inventory to " + fileName);
	}

	public static void main(String[] args) throws IOException {


		BasicAWSCredentials creds = new BasicAWSCredentials("A", "");

		client = AmazonGlacierClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				.build();

		try {

			InitiateJobRequest initJobRequest = new InitiateJobRequest()
					.withVaultName(vaultName)
					.withJobParameters(
							new JobParameters()
							.withType("inventory-retrieval")
							.withSNSTopic("arn:aws:sns:us-east-1:464:GlacierTopic")
							);

			InitiateJobResult initJobResult = client.initiateJob(initJobRequest);
			String jobId = initJobResult.getJobId();

			downloadJobOutput(jobId);

		} catch (Exception e)
		{
			System.err.println(e);
		}
	}
}
