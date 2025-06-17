# Chunk file upload

This project demonstrates a file upload system with support for parallel, chunked uploads. It is designed to efficiently handle large files by splitting them into smaller chunks and uploading them, improving reliability and performance.

## How Parallel Chunk Upload Works

### 1. Start Upload Session
- The client requests a new upload session from `/upload-chunk-parallel/start`.
- The server generates a unique `uploadId` and creates a directory for incoming chunks.

### 2. Split File into Chunks
- The frontend JavaScript splits the selected file into chunks (default: 200KB per chunk).

### 3. Upload Chunks (Can Be Parallelized)
- Each chunk is sent to `/upload-chunk-parallel/process` with:
  - `uploadId`
  - `chunkIndex` (order of the chunk)
  - `fileLength` (optional, the total length of the file)
  - `chunkLength`
  - The chunk data itself
- Multiple chunks can be uploaded in parallel for speed (the current implementation uploads sequentially, but can be easily parallelized by sending multiple requests at once).
- The server saves each chunk in a temporary directory, named by `chunkIndex`.

### 4. Complete Upload
- After all chunks are uploaded, the client calls `/upload-chunk-parallel/complete` with the `uploadId` and final file name.
- The server merges all chunks in order, assembles the final file, and cleans up temporary chunk files.

## Usage

1. Start server
2. Go to `/fe` folder run `index.html`
3. Select large file upload

## API Endpoints
- `POST /upload-chunk-parallel/start` – Start a new upload, returns `uploadId`.
- `POST /upload-chunk-parallel/process` – Upload a chunk (multipart/form-data).
- `POST /upload-chunk-parallel/complete` – Merge chunks and complete upload.

## Notes
- Chunk size can be adjusted in `fe/upload-parallel.js` (`chunkSize` variable).
- The backend ensures security by validating chunk paths and cleaning up after merging.
- This approach improves reliability for large files and allows for resumable uploads or retrying failed chunks in the future.

---

Feel free to extend this README with more details or usage examples as needed.