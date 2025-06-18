const startUploadingChunkUrl = `${baseUrl}/upload-chunk-parallel/start`;
const processUploadingChunkUrl = `${baseUrl}/upload-chunk-parallel/process`;
const completeUploadingChunkUrl = `${baseUrl}/upload-chunk-parallel/complete`;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById("recoveryFile");

    setupFileInputs(fileInput, 'recoveryFile');
    setupUploadButtons(fileInput);
});

function setupFileInputs(fileInput, fileInputId) {
    fileInput.addEventListener('change', (e) => handleFileSelect(e, fileInputId));
    // Drag and drop functionality
    setupDragAndDrop(fileInputId);
}

function setupUploadButtons(fileInput) {
    document.getElementById("recoveryFileUploadBtn").addEventListener('click', () => {
        handleUpload(fileInput);
    });
}

async function handleUpload(fileInput) {

    const uploadId = await startUploadingChunk();

    const file = fileInput.files[0];
    const fileName = file.name;

    const totalChunks = Math.ceil(file.size / chunkSize);

    // Iterate over the chunks and upload them sequentially
    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);

        const percentage = Math.round((chunkIndex + 1) / totalChunks * 100);
        setProgress("recoveryProgress", percentage);

        // Make an API call to upload the chunk to the backend
        await processUploadingChunk(uploadId, chunk, file.size, chunkIndex);
    }

    // Make an API call to complete the upload
    await completeUploadingChunk(uploadId, fileName);

    showStatus("recoveryStatus", "File uploaded successfully!", 'success');
};

async function startUploadingChunk() {
    try {
        const response = await fetch(startUploadingChunkUrl, {
            method: "POST"
        });

        if (!response.ok) {
            showStatus("recoveryStatus", `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
        const json = await response.json();
        return json.uploadId;
    } catch (error) {
        console.error('Upload error:', error);
        showStatus("recoveryStatus", `Upload failed: ${error.message}`, 'error');
    }
}

async function completeUploadingChunk(uploadId, fileName) {
    try {
        const response = await fetch(completeUploadingChunkUrl, {
            method: "POST",
            headers: {"Content-Type": "application/json",},
            body: JSON.stringify({
                uploadId: uploadId,
                fileName: fileName
            })
        });

        if (!response.ok) {
            showStatus("recoveryStatus", `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
    } catch (error) {
        console.error('Upload error:', error);
        showStatus("recoveryStatus", `Upload failed: ${error.message}`, 'error');
    }
}

async function processUploadingChunk(uploadId, file, fileLength, chunkIndex) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("uploadId", uploadId);
    formData.append("fileLength", fileLength);
    formData.append("chunkIndex", chunkIndex);
    formData.append("chunkLength", chunkSize);

    try {
        const response = await fetch(processUploadingChunkUrl, {
            method: "POST",
            body: formData,
        });

        if (!response.ok) {
            showStatus("recoveryStatus", `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
    } catch (error) {
        console.error('Upload error:', error);
        setProgress("recoveryProgress", 0);
        showStatus("recoveryStatus", `Upload failed: ${error.message}`, 'error');
    }
}
