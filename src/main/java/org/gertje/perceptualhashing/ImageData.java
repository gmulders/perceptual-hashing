package org.gertje.perceptualhashing;

import java.nio.file.Path;

import org.gertje.perceptualhashing.hash.CHash;
import org.gertje.perceptualhashing.hash.DHash;
import org.gertje.perceptualhashing.hash.PHash;
import org.gertje.perceptualhashing.hash.QHash;
import org.gertje.perceptualhashing.hash.ZHash;


public class ImageData {

    private final long id;
    private final DHash dHash;
    private final PHash pHash;
    private final QHash qHash;
    private final ZHash zHash;
    private final CHash cHash;
    private final Path path;
    private final long crc32;
    private final long size;
    private final long width;
    private final long height;

    public ImageData(long id, DHash dHash, PHash pHash, QHash qHash, ZHash zHash, CHash cHash, Path path, long crc32,
                     long size, long width, long height) {
        this.id = id;
        this.dHash = dHash;
        this.pHash = pHash;
        this.qHash = qHash;
        this.zHash = zHash;
        this.cHash = cHash;
        this.path = path;
        this.crc32 = crc32;
        this.size = size;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "id=" + id +
                ", dHash=" + dHash +
                ", pHash=" + pHash +
                ", qHash=" + qHash +
                ", zHash=" + zHash +
                ", cHash=" + cHash +
                ", path=" + path +
                ", crc32=" + crc32 +
                ", size=" + size +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageData imageData = (ImageData) o;

        if (crc32 != imageData.crc32) return false;
        if (size != imageData.size) return false;
        if (width != imageData.width) return false;
        if (height != imageData.height) return false;
        if (id != imageData.id) return false;
        if (!dHash.equals(imageData.dHash)) return false;
        if (!pHash.equals(imageData.pHash)) return false;
        if (!qHash.equals(imageData.qHash)) return false;
        if (!zHash.equals(imageData.zHash)) return false;
        if (!cHash.equals(imageData.cHash)) return false;
        return path.equals(imageData.path);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + dHash.hashCode();
        result = 31 * result + pHash.hashCode();
        result = 31 * result + qHash.hashCode();
        result = 31 * result + zHash.hashCode();
        result = 31 * result + cHash.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + (int) (crc32 ^ (crc32 >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (width ^ (width >>> 32));
        result = 31 * result + (int) (height ^ (height >>> 32));
        return result;
    }

    public long getId() {
        return id;
    }

    public DHash getDHash() {
        return dHash;
    }

    public PHash getPHash() {
        return pHash;
    }

    public QHash getQHash() {
        return qHash;
    }

    public ZHash getZHash() {
        return zHash;
    }

    public CHash getCHash() {
        return cHash;
    }

    public Path getPath() {
        return path;
    }

    public long getCrc32() {
        return crc32;
    }

    public long getSize() {
        return size;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }
}
