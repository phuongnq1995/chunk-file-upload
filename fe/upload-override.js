const startUploadingChunkOverrideUrl = `${baseUrl}/upload-chunk-override/start`;
const processUploadingChunkOverrideUrl = `${baseUrl}/upload-chunk-override/process`;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById("overrideFile");

    setupOverrideFileInputs(fileInput, 'overrideFile');
    setupOverrideUploadButtons(fileInput);
});

function setupOverrideFileInputs(fileInput, fileInputId) {
    fileInput.addEventListener('change', (e) => handleFileSelect(e, fileInputId));
    // Drag and drop functionality
    setupDragAndDrop(fileInputId);
}

function setupOverrideUploadButtons(fileInput) {
    document.getElementById("overrideFileUploadBtn").addEventListener('click', () => {
        handleUploadOverride(fileInput);
    });
}

async function handleUploadOverride(fileInput) {

    const uploadId = await startUploadingChunkOverride();

    const file = fileInput.files[0];
    const totalChunks = Math.ceil(file.size / chunkSize);

    // Iterate over the chunks and upload them sequentially
    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);

        const percentage = Math.round((chunkIndex + 1) / totalChunks * 100);
        setProgress("overrideProgress", percentage);

        // Make an API call to upload the chunk to the backend
        await processUploadingChunkOverride(uploadId, chunk, file.size, file.name, chunkIndex);
    }

    showStatus("overrideStatus", "File uploaded successfully!", 'success');
};

async function startUploadingChunkOverride() {
    try {
        const response = await fetch(startUploadingChunkOverrideUrl, {
            method: "POST"
        });

        if (!response.ok) {
            showStatus("overrideStatus", `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
        const json = await response.json();
        return json.uploadId;
    } catch (error) {
        console.error('Upload error:', error);
        showStatus("overrideStatus", `Upload failed: ${error.message}`, 'error');
    }
}

async function processUploadingChunkOverride(uploadId, file, fileLength, fileName, chunkIndex) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("uploadId", uploadId);
    formData.append("fileLength", fileLength);
    formData.append("fileName", fileName);
    formData.append("chunkIndex", chunkIndex);
    formData.append("chunkLength", chunkSize);

    try {
        const response = await fetch(processUploadingChunkOverrideUrl, {
            method: "POST",
            body: formData,
        });

        if (!response.ok) {
            showStatus("overrideStatus", `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
    } catch (error) {
        console.error('Upload error:', error);
        setProgress("overrideProgress", 0);
        showStatus("overrideStatus", `Upload failed: ${error.message}`, 'error');
    }
}
