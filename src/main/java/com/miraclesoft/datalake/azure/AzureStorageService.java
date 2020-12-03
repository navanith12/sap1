package com.miraclesoft.datalake.azure;

import java.io.File;
import java.net.URI;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * This sample illustrates basic usage of the Azure file storage service.
 */
public class AzureStorageService {

	// Configure the connection-string with your values
	public String storageConnectionString;

	public AzureStorageService(String accessKey, String accountName) {
		// Use the CloudStorageAccount object to connect to your storage account
		try {
			this.storageConnectionString = "DefaultEndpointsProtocol=https;" + "AccountName=" + accountName + ";"
					+ "AccountKey=" + accessKey + ";" + "EndpointSuffix=core.windows.net";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public URI uploadDownloadtoAzure(String container, String filePath, String action) {
		File sourceFile = null, downloadedFile = null;
		System.out.println("Azure Blob storage quick start sample");
		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		CloudBlobContainer containerClient = null;
		CloudBlockBlob blob = null;
		try {
			System.out.println("storageConnectionString:" + storageConnectionString);
			// Parse connection string & create a blob client to interact with Blob storage
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			System.out.println(storageAccount.getBlobEndpoint());
			blobClient = storageAccount.createCloudBlobClient();
			containerClient = blobClient.getContainerReference(container);
			// Create the container if it does not exist with public access.
			System.out.println("Creating container: " + containerClient.getName());
			containerClient.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(),
					new OperationContext());
			// Creating a sample file
			sourceFile = new File(filePath);
			// Getting a blob reference
			blob = containerClient.getBlockBlobReference(sourceFile.getName());

			// Creating blob and uploading file to it
			if (action.equals("upload")) {
				System.out.println("Uploading the sample file ");
				blob.uploadFromFile(sourceFile.getAbsolutePath());
				System.out.println("URI of blob is: " + blob.getUri());
				// Listing contents of container
//				for (ListBlobItem blobItem : containerClient.listBlobs()) {
//					System.out.println("URI of blob is: " + blobItem.getUri());
//				}
			}

			// Download blob. In most cases, you would have to retrieve the reference
			// to cloudBlockBlob here. However, we created that reference earlier, and
			// haven't changed the blob we're interested in, so we can reuse it.
			// Here we are creating a new file to download to. Alternatively you can also
			// pass in the path as a string into downloadToFile method:
			// blob.downloadToFile("/path/to/new/file").
			if (action.equals("download")) {
				downloadedFile = new File(sourceFile.getParentFile(), sourceFile.getName());
				blob.downloadToFile(downloadedFile.getAbsolutePath());
			}

		} catch (StorageException ex) {
			ex.printStackTrace();
//			System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s",
//					ex.getHttpStatusCode(), ex.getErrorCode()));
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(ex.getMessage());
		}
		return blob.getUri();
	}

}