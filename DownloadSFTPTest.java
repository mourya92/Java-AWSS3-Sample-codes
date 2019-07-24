import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * The class SFTPUtil containing uploading, downloading, checking if file exists
 * and deleting functionality using Apache Commons VFS (Virtual File System)
 * Library
 * 
 * @author Ashok
 * 
 */
public class DownloadSFTPTest {

    public static void main(String[] args) throws JSchException, IOException, SftpException {
        
    	String hostName = "";
        String username = "ssmourya";
        String password = "cognitotesttest";

        String localFilePath = "/Users/ssmourya/Documents/workspace/AWSTransferTest/src/image.jpg";
        String remoteFilePath = "image.jpg";       
      
        
        
		JSch jsch = new JSch();
		//File file = genereateFileFromKey(rsaKey);
		//jsch.addIdentity(file.getAbsolutePath());
		
		Session session = jsch.getSession("ssmourya", "", 22);
		session.setPassword("cognitotesttest");
		
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect(10000);
		session.setTimeout(600000);
		Channel channel = session.openChannel("sftp");
		channel.connect(10000);
		ChannelSftp channelSftp = (ChannelSftp) channel;

		File targetFile = new File("/Users/ssmourya/Documents/workspace/AWSTransferTest/src/test1.csv");
	    OutputStream outStream = new FileOutputStream(targetFile);
	    
		//String csv = IOUtils.toString(channelSftp.get("88f23685-e9f0-46ba-8da4-e04391c7154a.csv"), StandardCharsets.UTF_8);
		//System.out.println(csv);
		
	    System.out.println("Downloading");
	    
	    channelSftp.get("88f23685-e9f0-46ba-8da4-e04391c7154a.csv", outStream);
		
		System.out.println("Done");

		
		
        //String remoteTempFilePath = "image.jpg";

        //upload(hostName, username, password, localFilePath, remoteFilePath);
        //exist(hostName, username, password, remoteFilePath);
        
        //download(hostName, username, password, localFilePath,remoteFilePath);
       
        //move(hostName, username, password, remoteFilePath, remoteTempFilePath);
        //delete(hostName, username, password, remoteFilePath);
        
    }

    /**
     * Method to upload a file in Remote server
     * 
     * @param hostName
     *            HostName of the server
     * @param username
     *            UserName to login
     * @param password
     *            Password to login
     * @param localFilePath
     *            LocalFilePath. Should contain the entire local file path -
     *            Directory and Filename with \\ as separator
     * @param remoteFilePath
     *            remoteFilePath. Should contain the entire remote file path -
     *            Directory and Filename with / as separator
     */
    public static void upload(String hostName, String username, String password, String localFilePath, String remoteFilePath) {

        File file = new File(localFilePath);
        if (!file.exists())
            throw new RuntimeException("Error. Local file not found");

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());
            /*
             * use createDefaultOptions() in place of fsOptions for all default
             * options - Ashok.
             */

            // Copy local file to sftp server
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            System.out.println("File upload success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public static boolean move(String hostName, String username, String password, String remoteSrcFilePath, String remoteDestFilePath){
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteSrcFilePath), createDefaultOptions());
            FileObject remoteDestFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteDestFilePath), createDefaultOptions());

            if (remoteFile.exists()) {
                remoteFile.moveTo(remoteDestFile);;
                System.out.println("Move remote file success");
                return true;
            }
            else{
                System.out.println("Source file doesn't exist");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    /**
     * Method to download the file from remote server location
     * 
     * @param hostName
     *            HostName of the server
     * @param username
     *            UserName to login
     * @param password
     *            Password to login
     * @param localFilePath
     *            LocalFilePath. Should contain the entire local file path -
     *            Directory and Filename with \\ as separator
     * @param remoteFilePath
     *            remoteFilePath. Should contain the entire remote file path -
     *            Directory and Filename with / as separator
     */
    public static void download(String hostName, String username, String password, String localFilePath, String remoteFilePath) {

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Append _downlaod_from_sftp to the given file name.
            //String downloadFilePath = localFilePath.substring(0, localFilePath.lastIndexOf(".")) + "_downlaod_from_sftp" + localFilePath.substring(localFilePath.lastIndexOf("."), localFilePath.length());

            // Create local file object. Change location if necessary for new downloadFilePath
            FileObject localFile = manager.resolveFile(localFilePath);

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());

            // Copy local file to sftp server
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

            System.out.println("File download success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    /**
     * Method to delete the specified file from the remote system
     * 
     * @param hostName
     *            HostName of the server
     * @param username
     *            UserName to login
     * @param password
     *            Password to login
     * @param localFilePath
     *            LocalFilePath. Should contain the entire local file path -
     *            Directory and Filename with \\ as separator
     * @param remoteFilePath
     *            remoteFilePath. Should contain the entire remote file path -
     *            Directory and Filename with / as separator
     */
    public static void delete(String hostName, String username, String password, String remoteFilePath) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());

            if (remoteFile.exists()) {
                remoteFile.delete();
                System.out.println("Delete remote file success");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    // Check remote file is exist function:
    /**
     * Method to check if the remote file exists in the specified remote
     * location
     * 
     * @param hostName
     *            HostName of the server
     * @param username
     *            UserName to login
     * @param password
     *            Password to login
     * @param remoteFilePath
     *            remoteFilePath. Should contain the entire remote file path -
     *            Directory and Filename with / as separator
     * @return Returns if the file exists in the specified remote location
     */
    public static boolean exist(String hostName, String username, String password, String remoteFilePath) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(createConnectionString(hostName, username, password, remoteFilePath), createDefaultOptions());

            System.out.println("File exist: " + remoteFile.exists());

            return remoteFile.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    /**
     * Generates SFTP URL connection String
     * 
     * @param hostName
     *            HostName of the server
     * @param username
     *            UserName to login
     * @param password
     *            Password to login
     * @param remoteFilePath
     *            remoteFilePath. Should contain the entire remote file path -
     *            Directory and Filename with / as separator
     * @return concatenated SFTP URL string
     */
    public static String createConnectionString(String hostName, String username, String password, String remoteFilePath) {
        return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
    }

    /**
     * Method to setup default SFTP config
     * 
     * @return the FileSystemOptions object containing the specified
     *         configuration options
     * @throws FileSystemException
     */
    public static FileSystemOptions createDefaultOptions() throws FileSystemException {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // SSH Key checking
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        /*
         * Using the following line will cause VFS to choose File System's Root
         * as VFS's root. If I wanted to use User's home as VFS's root then set
         * 2nd method parameter to "true"
         */
        
        // Root directory set to user home
        //SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

        return opts;
    }
}
