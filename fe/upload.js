const baseUrl = 'http://localhost:8080';
let uploadedFiles = [];
const chunkSize = 200 * 1024; // Set the desired chunk size (200KB in this example)

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    setupFileInputs();
    setupUploadButtons();
    displayUploadedFiles();
});

function setupFileInputs() {
    const recoveryInput = document.getElementById('recoveryFile');
    const overrideInput = document.getElementById('overrideFile');

    recoveryInput.addEventListener('change', (e) => handleFileSelect(e, 'recovery'));
    overrideInput.addEventListener('change', (e) => handleFileSelect(e, 'override'));

    // Drag and drop functionality
    setupDragAndDrop('recoveryFile', 'recovery');
    setupDragAndDrop('overrideFile', 'override');
}

function setupDragAndDrop(inputId, type) {
    const label = document.querySelector(`label[for="${inputId}"]`);

    label.addEventListener('dragover', (e) => {
        e.preventDefault();
        label.style.borderColor = '#667eea';
        label.style.background = '#f5f7ff';
    });

    label.addEventListener('dragleave', (e) => {
        e.preventDefault();
        label.style.borderColor = '#ccc';
        label.style.background = '#fafafa';
    });

    label.addEventListener('drop', (e) => {
        e.preventDefault();
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            document.getElementById(inputId).files = files;
            handleFileSelect({ target: { files: files } }, type);
        }
        label.style.borderColor = '#ccc';
        label.style.background = '#fafafa';
    });
}

function handleFileSelect(event, type) {
    const file = event.target.files[0];
    if (!file) return;

    const fileInfo = document.getElementById(`${type}FileInfo`);
    const uploadBtn = document.getElementById(`${type}UploadBtn`);

    // Display file information
    fileInfo.innerHTML = `
        <div style="display: flex; align-items: center; margin-bottom: 10px;">
            <div style="font-size: 24px; margin-right: 10px;">${getFileIcon(file.type)}</div>
            <div>
                <div style="font-weight: 600; color: #333;">${file.name}</div>
                <div style="color: #666; font-size: 14px;">
                    ${formatFileSize(file.size)} • ${file.type || 'Unknown type'}
                </div>
            </div>
        </div>
    `;

    fileInfo.classList.add('show');

    // Enable upload button when both file and document type are selected
    checkUploadReady(type);
}

function setupUploadButtons() {
    document.getElementById('recoveryUploadBtn').addEventListener('click', () => {
        handleUpload('recovery');
    });

    document.getElementById('overrideUploadBtn').addEventListener('click', () => {
        handleUpload('override');
    });
}

function checkUploadReady(type) {
    const fileInput = document.getElementById(`${type}File`);
    const uploadBtn = document.getElementById(`${type}UploadBtn`);

    const hasFile = fileInput.files.length > 0;

    uploadBtn.disabled = !(hasFile);
}

async function handleUpload(type) {
    const fileInput = document.getElementById(`${type}File`);
    const file = fileInput.files[0];

    const totalChunks = Math.ceil(file.size / chunkSize);

    // Iterate over the chunks and upload them sequentially
    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);

        const percentage = Math.round((chunkIndex + 1) / totalChunks * 100);
        setProgress(type, percentage);

        // Make an API call to upload the chunk to the backend
        await uploadChunk(type, chunk, file.size, file.name, chunkIndex);
    }

    showStatus(type, `File uploaded successfully! ${type === 'recovery' ? 'Backup created.' : 'File overridden.'}`, 'success');
};

async function uploadChunk(type, file, fileLength, fileName, chunkIndex) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("fileLength", fileLength);
    formData.append("fileName", fileName);
    formData.append("chunkIndex", chunkIndex);
    formData.append("chunkLength", chunkSize);

    try {
        const response = await fetch(`${baseUrl}/upload-chunk-${type}`, {
            method: "POST",
            body: formData,
        });

        if (!response.ok) {
            showStatus(type, `Upload failed: `, 'error');
            throw new Error("Error uploading chunk.");
        }
    } catch (error) {
        console.error('Upload error:', error);
        setProgress(type, 0);
        showStatus(type, `Upload failed: ${error.message}`, 'error');
    }
}

function displayUploadedFiles() {
    const uploadedFilesDiv = document.getElementById('uploadedFiles');
    const fileList = document.getElementById('fileList');

    if (uploadedFiles.length === 0) {
        uploadedFilesDiv.style.display = 'none';
        return;
    }

    uploadedFilesDiv.style.display = 'block';
    fileList.innerHTML = '';

    uploadedFiles.forEach(file => {
        const fileItem = document.createElement('div');
        fileItem.className = 'file-item';
        fileItem.innerHTML = `
            <div class="file-details">
                <div class="file-type-icon ${file.type}">${file.type === 'recovery' ? '🔄' : '⚡'}</div>
                <div>
                    <div style="font-weight: 600; color: #333;">${file.name}</div>
                    <div style="color: #666; font-size: 14px;">
                        ${file.documentType} • ${formatFileSize(file.size)} • ${formatDate(file.uploadDate)}
                    </div>
                </div>
            </div>
            <button class="download-btn" onclick="downloadFile('${file.fileKey}', '${file.name}')">
                Download
            </button>
        `;
        fileList.appendChild(fileItem);
    });
}

function setProgress(type, percentage) {
    const progressBar = document.getElementById(`${type}Progress`);
    const progressFill = progressBar.querySelector('.progress-fill');

    progressBar.classList.add('show');
    progressFill.style.width = `${percentage}%`;

    if (percentage === 100) {
        setTimeout(() => {
            progressBar.classList.remove('show');
            progressFill.style.width = '0%';
        }, 2000);
    }
}

function showStatus(type, message, statusType) {
    const statusDiv = document.getElementById(`${type}Status`);
    statusDiv.textContent = message;
    statusDiv.className = `status-message show ${statusType}`;

    if (statusType === 'success') {
        setTimeout(() => {
            statusDiv.classList.remove('show');
        }, 5000);
    }
}

// Utility functions
function getFileIcon(mimeType) {
    if (mimeType.startsWith('image/')) return '🖼️';
    if (mimeType === 'application/pdf') return '📄';
    return '📁';
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}