import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;

//Picture class from homework 7. No explanation needed. I use blur and mirror in different situations with certain sprites.

public class PictureObj {
	/*
	 * This file is adpted by UPenn CIS 120 course staff from code by
	 * Richard Wicentowski and Tia Newhall (2005)
	 * Modified by Fernando Pereira to accept URLs and other resource locators
	 */


	/**
	 * An image represented by a 2D array of Pixels. 
	 *
	 * Pictures are immutable. Although they provide access to a 2D 
	 * array of pixels, this array is a copy of the one stored in the Picture.
	 * The original image cannot be modified.
	 */ 

		private BufferedImage bufferedImage;
		private WritableRaster raster;

		/**
		 * Copies a Picture.
		 * 
		 * @param other Picture the other Picture to copy
		 */ 
		public PictureObj(PictureObj other) {
			bufferedImage = 
				new BufferedImage(other.getWidth(),
							      other.getHeight(),
							      BufferedImage.TYPE_INT_RGB);

			raster = bufferedImage.getRaster();
			raster.setRect(other.bufferedImage.getRaster());
		}

		/** 
		 * Creates a Picture by loading the given file or URL.
		 *  
		 * @param filename the location of the image file to read
		 */ 
		public PictureObj(String filename) {
			load(filename);
		}

		/**
		 * Creates a picture given a bitmap. The bitmap should be in left-to-right,
		 * top-to-bottom ordering.
		 * 
		 * @param bmp The bitmap
		 */
		public PictureObj(Pixel[][] bmp) {
			setBitmap(bmp);
		}

		/** 
		 * Get the width of the image.
		 */ 
		public int getWidth() { return bufferedImage.getWidth(); }
		
		/** 
		 * Get the height of the image.
		 */ 
		public int getHeight() { return bufferedImage.getHeight(); }

		private void load(String filename) {
			ImageIcon icon;

			try {
				if ((new File(filename)).exists())
					icon = new ImageIcon(filename);
				else {
					java.net.URL u = new java.net.URL(filename);
					icon = new ImageIcon(u);
				}
			} catch (Exception e) { throw new RuntimeException(e); }
			
			Image image = icon.getImage();
			bufferedImage = 
				new BufferedImage(image.getWidth(null),
							      image.getHeight(null),
							      BufferedImage.TYPE_INT_RGB);
			Graphics g = bufferedImage.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			raster = bufferedImage.getRaster();
		}

		private void setBitmap(Pixel[][] bmp) {
			int w = bmp.length;

			if (w == 0) {
				throw new IndexOutOfBoundsException("expected non-empty image, got width of 0");
			}

			int h = bmp[0].length;
			if (h == 0) {
				throw new IndexOutOfBoundsException("expected non-empty image, got width of 0");
			}

			bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			raster = bufferedImage.getRaster();

			for (int x = 0; x < w; x++) {
				if (bmp[x].length != h) {
					throw new IndexOutOfBoundsException("ragged image, found height of " + h + " and " + bmp[x].length);
				}

				for (int y = 0; y < h; y++) {
					raster.setPixel(x, y, bmp[x][y].getComponents());
				}
			}
		}

		/**
		 * Gets a bitmap (i.e., matrix of pixels) of the image. This method returns
		 * a copy of the image's contents---editing the returned bitmap will not
		 * affect the Picture.
		 * 
		 * The bitmap is in a left-to-right, top-to-bottom order. The first index is
		 * the row, the second index is the column.
		 * 
		 * @return a left-to-right, top-to-bottom array of arrays of Pixels
		 */
		public Pixel[][] getBitmap() {
			int w = getWidth();
			int h = getHeight();

			Pixel[][] bmp = new Pixel[w][h];

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					bmp[x][y] = new Pixel(raster.getPixel(x, y, (int[]) null));
				}
			}

			return bmp;
		}
		
		/**
		 * Creates an ImageIcon, suitable for display by Swing components.
		 * 
		 * @return ImageIcon pointing at a copy of this image
		 */
		public Image toImage() {
			PictureObj copy = new PictureObj(this);
			return new ImageIcon(copy.bufferedImage).getImage();
		}

		/**
		 * Determine how different a Picture is from this one.
		 *
		 * @param other The other picture to compare.
		 * @return The difference in the sum of the raster samples of all pixels.
		 */
		public double distance(PictureObj other) {
			int w = getWidth();
			int h = getHeight();

			if (w != other.getWidth() ||
			    h != other.getHeight() ||
				raster.getNumBands() != other.raster.getNumBands())
				return Double.POSITIVE_INFINITY;

			double dist = 0;
			int depth = raster.getNumBands();

			int[] pThis = new int[depth];
			int[] pOther = new int[depth];

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					raster.getPixel(i, j, pThis);
					other.raster.getPixel(i, j, pOther);
					for (int k = 0; k < depth; k++)
						dist += Math.abs(pThis[k]-pOther[k])/255.0;
				}
			}

			return dist/(w*h*depth);
		}

		private static Pattern suffix = Pattern.compile(".*\\.(\\w{3,4})");
		public void save(String filename) {
			String type = "png";

			// detect the file type
			Matcher m = suffix.matcher(filename);
			if (m.matches()) {
				type = m.group(1);
			}

			try {
				ImageIO.write(bufferedImage, type, new File(filename)); 
			} catch(IOException e) { 
				throw new RuntimeException(e); 
			}

		}


}
