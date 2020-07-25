package org.gertje.perceptualhashing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import javax.imageio.ImageIO;

import org.gertje.perceptualhashing.hash.CHash;
import org.gertje.perceptualhashing.hash.DCTHashBuilder;
import org.gertje.perceptualhashing.hash.DHash;
import org.gertje.perceptualhashing.hash.PHash;
import org.gertje.perceptualhashing.hash.QHash;
import org.gertje.perceptualhashing.hash.ZHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageIndexer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageIndexer.class);

    private static final AtomicLong COUNTER = new AtomicLong(0);

    public static void main(String[] args) throws IOException {
        Path path = Paths.get(args[0]);

        LOGGER.info("Starting image hashing");
        ImageIO.setUseCache(false);

        List<ImageData> imageDataList = findImages(path)
                .parallel()
                .map(ImageIndexer::createImageData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        imageDataList.forEach(data -> LOGGER.info("{}", data));

        LOGGER.info("Finished image hashing");
    }

    private static ImageData createImageData(Path path) {
        LOGGER.info("Read path: {}", path);
        long start = System.currentTimeMillis();
        File file = path.toFile();
        BufferedImage image;
        long size;
        long afterImageRead;
        long afterSize;
        CRC32 crc32 = new CRC32();
        try (InputStream in = new CheckedInputStream(new FileInputStream(file), crc32)) {
            image = ImageIO.read(in);
            afterImageRead = System.currentTimeMillis();
            size = Files.size(path);
            afterSize = System.currentTimeMillis();
        } catch (IOException e) {
            LOGGER.error("Could not read image: {}", file, e);
            return null;
        }

        if (image == null) {
            LOGGER.error("Could not read image: {}", file);
            return null;
        }

        DHash dHash = DHash.calculate(image);
        long afterDHash = System.currentTimeMillis();

        DCTHashBuilder builder = DCTHashBuilder
                .builder(image)
                .log()
                ;
        long afterBuilder = System.currentTimeMillis();

        PHash pHash = builder.pHash();
        long afterPHash = System.currentTimeMillis();

        QHash qHash = builder.qHash();
        long afterQHash = System.currentTimeMillis();

        ZHash zHash = builder.zHash();
        long afterZHash = System.currentTimeMillis();

        CHash cHash = builder.cHash();
        long afterCHash = System.currentTimeMillis();

        LOGGER.debug("dHash: {}, pHash: {}, zHash: {}, cHash: {}, crc32: {}", dHash, pHash, zHash, cHash, crc32.getValue());

        LOGGER.debug("Time - IO: {}, Size: {}, dHash: {}, dct: {}, pHash: {}, qHash: {}, zHash: {}, cHash: {}",
                afterImageRead - start,
                afterSize - afterImageRead,
                afterDHash - afterSize,
                afterBuilder - afterDHash,
                afterPHash - afterBuilder,
                afterQHash - afterPHash,
                afterZHash - afterQHash,
                afterCHash - afterZHash
        );
        return new ImageData(COUNTER.getAndIncrement(), dHash, pHash, qHash, zHash, cHash, path, crc32.getValue(),
                size, image.getWidth(), image.getHeight());
    }

    private static Stream<Path> findImages(Path path) throws IOException {
        return Files.find(path,
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && hasImageSuffix(filePath));
    }

    private static boolean hasImageSuffix(Path filePath) {
        String lowerCasePath = filePath.toString().toLowerCase();
        return lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg");
    }
}
