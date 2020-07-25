# Perceptual Hashing

This repository contains the code for the hashing algorithms that I investigated as a way to compare images for
equality, a tool to calculate all hashes over all images contained in a directory, and a class to cluster a large set of
images into smaller clusters based on some distance.

I left out all the small Java programs to compare, test, run, etc, Python scripts to check and plot the data, and a
simple web-based tool for me to (humanly) compare images on equality to create a test set.

## About
Perceptual hashing is about creating hashes of images. A perceptual hash differs from a "normal" hash (e.g. crc32) in
that similar sources result in similar hashes. So two images that are almost the same will result in a hash that is
almost the same. We can use the Hamming distance between two hashes as a measure for equality between two images. (where
a lower distance means that two images are more alike.)

## Algorithms
In my need to deduplicate image libraries I searched for a good working hashing algorithm. On the internet there are a
lot of resources naming the _pHash_ and _dHash_ algorithms. Both these algorithms perform relatively well; the _pHash_
algorithm a little better, but the _dHash_ is much faster.

From here I started investigating ways to improve on the _pHash_ algorithm. I devised three different algorithms of
which some perform better than others:
- zHash: DCT based algorithm that takes the lowest frequencies based on the ZigZag order also used in JPEG
- cHash: DCT based algorithm that takes the two color channels and uses ZigZag order as well
- qHash: DCT based algorithm that uses a scaled up version of the default JPEG quantization table to find the
  frequencies that humans are most "sensitive" to

## Performance
Comparing the algorithms is hard. Some perform better on photos while other algorithms perform better on "screenshots"
made on my mobile phone (mostly text). In my test set it seems though that _pHash_, _zHash_ and _qHash_ have a similar
distribution for equal images (i.e. almost all pairs of equal images have distance 0, with some exceptions with slightly
larger distances). The distribution for distances of pairs of unequal images the distribution of _qHash_ is smaller
spread around the mean than for _zHash_, which in turn is smaller spread than for _pHash_.

## Why do these DCT based algorithms work?
To understand we can look at JPEG. It compresses images (photos) by leaving out details that the human eye (and brain)
cannot see.

Firstly JPEG converts colors into the YCbCr colorspace. This is done so that every channel (Y, Cb and Cr) can be
compressed separately. Since the human eye is more sensitive to luminosity (the Y channel) than for color (Cb and Cr
channels), the latter can be compressed more aggressively. This is why all algorithms except the _cHash_ work on a black
and white version of the image; it contains more information for the eye.

Next JPEG splits the image into 8x8 blocks and applies the DCT on these blocks. The DCT converts an image from the
"spatial domain" into the "frequency domain". To oversimplify; it decomposes the images into the frequencies it is made
of. This decomposition leads to an 8x8 matrix that contains the amplitudes of all possible frequencies. The human eye is
(oversimplified) more sensitive to low frequencies than to higher frequencies. This is why the _pHash_ algorithm (it
takes the lowest 8x8 frequencies as a square) and the _zHash_ (it takes the lowest frequencies in a ZigZag order)
algorithm work.

After applying the DCT, JPEG does something called
"[quantization](https://en.wikipedia.org/wiki/Quantization_(image_processing))"; it does an integer division (division
without a remainder) on every element in the 8x8 frequency matrix. This is where the lossy part in JPEG happens. The
divisor is a number from the so called quantization table. For lower frequencies the divisor is low and for higher
frequencies the divisor is higher. This will result in more compression for higher frequencies, which is great, because
the human eye cannot see that detail anyway. The quantization table basically tells us what frequencies the human eye is
most sensitive for. This is why the _qHash_ algorithm works; we take exactly those frequencies that correspond to the
lowest values in the (scaled up version) of the default JPEG quantization table.

These algorithms are less performant on the screenshots mentioned before. Because these contain letters, which are high
frequencies.

## Future work
Some ideas:
- Investigate how combining several algorithms might improve the hash
- JPEG2000 uses wavelets for compression; how can I use wavelets for creating hashes?

