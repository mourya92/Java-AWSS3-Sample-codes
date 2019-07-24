/**
 * 
 */

/**
 * @author ssmourya
 *
 */
public class TransferCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DefaultAWSCredentialsProviderChain credentialProviderChain = new DefaultAWSCredentialsProviderChain();
		 TransferManager tx = new TransferManager(
		                credentialProviderChain.getCredentials());
		 Upload myUpload = tx.upload(myBucket, myFile.getName(), myFile);

		 // You can poll your transfer's status to check its progress
		 if (myUpload.isDone() == false) {
		        System.out.println("Transfer: " + myUpload.getDescription());
		        System.out.println("  - State: " + myUpload.getState());
		        System.out.println("  - Progress: "
		                        + myUpload.getProgress().getBytesTransferred());
		 }

		 // Transfers also allow you to set a <code>ProgressListener</code> to receive
		 // asynchronous notifications about your transfer's progress.
		 myUpload.addProgressListener(myProgressListener);

		 // Or you can block the current thread and wait for your transfer to
		 // to complete. If the transfer fails, this method will throw an
		 // AmazonClientException or AmazonServiceException detailing the reason.
		 myUpload.waitForCompletion();

		 // After the upload is complete, call shutdownNow to release the resources.
		 tx.shutdownNow();
		 
	}

}
