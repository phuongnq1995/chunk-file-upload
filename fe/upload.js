async function uploadChunk(file, fileLength, fileName, chunkIndex) {
    const formData = new FormData();
    formData.append("file", file);
	formData.append("fileLength", fileLength);
	formData.append("fileName", fileName);
    formData.append("chunkIndex", chunkIndex);

    try {
        const response = await fetch("http://localhost:8080/upload", {
            method: "POST",
            body: formData,
        });

        if (!response.ok) {
            document.getElementById("status").textContent = "failed";
            throw new Error("Error uploading chunk.");
        }
    } catch (error) {
        document.getElementById("status").textContent = "failed";
        console.error(error);
    }
}

const importInvoices = () => {
    document.getElementById("file-input").click();
    // Example usage
    const fileInput = document.getElementById("file-input");
    fileInput.addEventListener("change", async (event) => {
        document.getElementById("status").textContent = "uploading";
        const file = event.target.files[0];
        const chunkSize = 2 * 1024 * 1024; // Set the desired chunk size (2MB in this example)
        const totalChunks = Math.ceil(file.size / chunkSize);

        // Iterate over the chunks and upload them sequentially
        for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            const start = chunkIndex * chunkSize;
            const end = Math.min(start + chunkSize, file.size);
            const chunk = file.slice(start, end);

            // Make an API call to upload the chunk to the backend
            await uploadChunk(chunk, file.size, file.name, chunkIndex);
        }
        document.getElementById("status").textContent = "successfully";
    });
};

document.getElementById("btn-import").addEventListener("click", (event) => {
    event.preventDefault();
    importInvoices();
});
