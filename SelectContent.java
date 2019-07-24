import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CSVInput;
import com.amazonaws.services.s3.model.CSVOutput;
import com.amazonaws.services.s3.model.CompressionType;
import com.amazonaws.services.s3.model.ExpressionType;
import com.amazonaws.services.s3.model.InputSerialization;
import com.amazonaws.services.s3.model.OutputSerialization;
import com.amazonaws.services.s3.model.SelectObjectContentEvent;
import com.amazonaws.services.s3.model.SelectObjectContentEventVisitor;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectObjectContentResult;
import com.amazonaws.util.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.amazonaws.util.IOUtils.copy;

/**
 * This example shows how to query data from S3Select and consume the response in the form of an
 * InputStream of records and write it to a file.
 */

public class SelectContent {

    private static final String BUCKET_NAME = "xxxxxxxxxxxxxxxxx";
    private static final String CSV_OBJECT_KEY = "OPM_Output_transport_sample.csv";
    private static final String S3_SELECT_RESULTS_PATH = "testoutput.csv";
    private static final String QUERY = "select * from s3object s where s._2='dwdm';";
    private static InputStream resultInputStream = null;
    
	public static void main(String[] args) throws Exception {
        
        BasicAWSCredentials creds = new BasicAWSCredentials("AKIAxxxxxxxxxxxxxxxxxx4SQ", "20CsxxxxxxxxxxxxxxxxxxxxxxxxxxxEla");
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.US_EAST_1)
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				.build();
		
		//final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
		
        SelectObjectContentRequest request = generateBaseCSVRequest(BUCKET_NAME, CSV_OBJECT_KEY, QUERY);
        final AtomicBoolean isResultComplete = new AtomicBoolean(false);

        try (OutputStream fileOutputStream = new FileOutputStream(new File (S3_SELECT_RESULTS_PATH));
             SelectObjectContentResult result = s3Client.selectObjectContent(request)) {
             resultInputStream = result.getPayload().getRecordsInputStream(
                    new SelectObjectContentEventVisitor() {
                        
                    	@Override
                        public void visit(SelectObjectContentEvent.StatsEvent event)
                        {
                            System.out.println(
                                    "Received Stats, Bytes Scanned: " + event.getDetails().getBytesScanned()
                                            +  " Bytes Processed: " + event.getDetails().getBytesProcessed());
                        }

                        /*
                         * An End Event informs that the request has finished successfully.
                         */
                        @Override
                        public void visit(SelectObjectContentEvent.EndEvent event)
                        {
                            isResultComplete.set(true);
                            
                            System.out.println("Received End Event. Result is complete.");
                        }
                    }
            );                      
             
             byte[] resultInputBytes = IOUtils.toByteArray(resultInputStream);
             
             /*
              * You need duplicate streams because, if you read the bytes from one stream, you cannot use the same stream to write to a file. 
              */
             
             InputStream inpStreamforFile = new ByteArrayInputStream(resultInputBytes);
             InputStream inpStreamforInline = new ByteArrayInputStream(resultInputBytes);
             
             copy(inpStreamforFile, fileOutputStream);
             
             String result_lines = new BufferedReader(new InputStreamReader(inpStreamforInline)).lines().collect(Collectors.joining("\n"));
             System.out.println(result_lines);
            
        }

        /*
         * The End Event indicates all matching records have been transmitted.
         * If the End Event is not received, the results may be incomplete.
         */
        if (!isResultComplete.get()) {
            throw new Exception("S3 Select request was incomplete as End Event was not received.");
        }
    }

    private static SelectObjectContentRequest generateBaseCSVRequest(String bucket, String key, String query) {
        SelectObjectContentRequest request = new SelectObjectContentRequest();
        request.setBucketName(bucket);
        request.setKey(key);
        request.setExpression(query);
        request.setExpressionType(ExpressionType.SQL);

        InputSerialization inputSerialization = new InputSerialization();
        inputSerialization.setCsv(new CSVInput());
        inputSerialization.setCompressionType(CompressionType.NONE);
        request.setInputSerialization(inputSerialization);

        OutputSerialization outputSerialization = new OutputSerialization();
        outputSerialization.setCsv(new CSVOutput());
        request.setOutputSerialization(outputSerialization);

        return request;
    }
}