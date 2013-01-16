
//Manipulate class from Homework 7. I use blur and mirror.

/**
 * The purpose of this assignment is to (re-) acquaint you with Java.
 *
 * You should fill in the body of the functions of this class. We have provided
 * a framework which will use these functions to manipulate images. As always,
 * you can add helper functions if you want. However, now that we're working in
 * Java, it is a good idea to make helper functions "private" (sort of like not
 * including functions in the MLI file in OCaml).
 * 
 * You will need to look at the provided Picture.java and Pixel.java files to
 * see if there are any helpful functions in them. However, you should not
 * modify these files, and you don't need to understand the code in each
 * function - you just need to understand how to use any functions you need.
 * Similarly, you will need to use two provided classes, ColorMap and IntQueue,
 * in order to complete the palette() and flood() functions, respectively.
 * Instructions for using those classes are included with the instructions for
 * each problem.
 * 
 * In each problem, don't modify the original picture. You should create a copy
 * of the picture passed to each function, modify it, and return it.
 *
 * Hint: think of a picture as a 2-dimensional array of Pixels. This
 * representation of images is called a Bitmap.
 */

public class Manipulate {
 
   /**
    * Rotate a picture 90 degrees clockwise.
    * 
    * For example, consider this bitmap, where each pixel is labeled by its
    * coordinates:
    * 
    * (0, 0)   (0, 1)   (0, 2)
    * (1, 0)   (1, 1)   (1, 2)
    * 
    * Rotating this bitmap will produce the following bitmap, with relabeled
    * coordinates:
    * 
    * (1, 0)   (0, 0)
    * (1, 1)   (0, 1)
    * (1, 2)   (0, 2)
    * 
    * Your job is to implement this "relabeling," copying pixels from their
    * old coordinates to their new coordinates.
    *
    * @param p The original picture to rotate.
    * @return The rotated picture.
    */

   public static PictureObj rotateCW(PictureObj p) {
      int w = p.getWidth();
      int h = p.getHeight();

      Pixel[][] src = p.getBitmap();
      Pixel[][] tgt = new Pixel[h][w]; // swap coordinates

      for (int x = 0; x < w; x++) {
         for (int y = 0; y < h; y++) {
            tgt[h - y - 1][x] = src[x][y]; // swap coordinates
         }
      }

      return new PictureObj(tgt);
   }

   /**
    * Rotate a picture 90 degrees counter-clockwise.
    * 
    * For example, consider this bitmap, where each pixel is labeled by its
    * coordinates:
    * 
    * (0, 0)   (0, 1)   (0, 2)
    * (1, 0)   (1, 1)   (1, 2)
    * 
    * Rotating this bitmap will produce the following bitmap, with relabeled
    * coordinates:
    * 
    * (0, 2)   (1, 2)
    * (0, 1)   (1, 1)
    * (0, 0)   (1, 0)
    * 
    * Your job is to implement this "relabeling," copying pixels from their
    * old coordinates to their new coordinates.
    *
    * @param p The original picture to rotate.
    * @return The rotated picture.
    */
   public static PictureObj rotateCCW(PictureObj p) {
	   int w = p.getWidth();
	   int h = p.getHeight();

	   Pixel[][] src = p.getBitmap();
	   Pixel[][] tgt = new Pixel[h][w];

	   for (int x = 0; x < w; x++) {
		   for (int y = 0; y < h; y++) {
			   tgt[y][w - x - 1] = src[x][y];
	       }
	   }
	   
      return new PictureObj(tgt);
   }

   /**
    * Flip a picture from top to bottom.
    * 
    * For example, consider this bitmap, where each pixel is labeled by its
    * coordinates:
    * 
    * (0, 0)   (0, 1)   (0, 2)
    * (1, 0)   (1, 1)   (1, 2)
    * 
    * Rotating this bitmap will produce the following bitmap, with relabeled
    * coordinates:
    * 
    * (1, 0)   (1, 1)   (1, 2)
    * (0, 0)   (0, 1)   (0, 2)
    * 
    * Your job is to implement this "relabeling," copying pixels from their
    * old coordinates to their new coordinates.
    *
    * @param p
    * @return the new picture
    */
   public static PictureObj mirrorVertical(PictureObj p) {
	   int w = p.getWidth();
	   int h = p.getHeight();

	   Pixel[][] src = p.getBitmap();
	   Pixel[][] tgt = new Pixel[w][h]; 

	   for (int x = 0; x < w; x++){
		   for (int y = 0; y < h; y++){
			   tgt[x][y] = src[x][h - y - 1];
		   }
	   }
	   
      return new PictureObj(tgt);
   }

   /**
    * Flip a picture from side to side.
    * 
    * For example, consider this bitmap, where each pixel is labeled by its
    * coordinates:
    * 
    * (0, 0)   (0, 1)   (0, 2)
    * (1, 0)   (1, 1)   (1, 2)
    * 
    * Rotating this bitmap will produce the following bitmap, with relabeled
    * coordinates:
    * 
    * (0, 2)   (0, 1)   (0, 0)
    * (1, 2)   (1, 1)   (1, 0)
    * 
    * Your job is to implement this "relabeling," copying pixels from their
    * old coordinates to their new coordinates.
    *
    * @param p
    * @return the mirrored picture
    */
   public static PictureObj mirrorHorizontal(PictureObj p) {
	   int w = p.getWidth();
	   int h = p.getHeight();

	   Pixel[][] src = p.getBitmap();
	   Pixel[][] tgt = new Pixel[w][h]; 

	   for (int x = 0; x < w; x++){
		   for (int y = 0; y < h; y++){
			   tgt[x][y] = src[w - x - 1][y];
		   }
	   }
	   
      return new PictureObj(tgt);
   }


   /**
    * Blur an image.
    *
    * There are different blurring algorithms, but we'll use the simplest:
    * box blur. Box blurring works by averaging the box-shaped neighborhood
    * around a pixel. The size of the box is configurable by setting the
    * radius, half the length of a side of the box. In the simplest case - a
    * radius of 1 - blurring just takes the average around a pixel. For
    * example, to blur around the pixel at (1,1) with radius 1, we take the
    * average value of the pixels of its neighborhood: (0,0) though (2,2),
    * including (1,1).
    *
    * Each Pixel is composed of 3 colors (red, green, and blue, or RGB) called
    * components. Each component should be averaged separately. For a radius
    * R, we set component c at location (x,y) according to the formula below:
    *
    * c = sum(c of each pixel within radius R) / ((2R + 1) * (2R + 1))
    *
    * Logically, it makes sense that this would be the formula for the
    * average. We sum the amount of each color in neighboring pixels and then
    * divide by the number of pixels we considered in the neighborhood.
    *
    * Note that this equation disregards corner cases. When blurring (0,0)
    * with radius 1, we only need to consider the top-left corner, (0,0)
    * through (1,1) - we'll divide by 4 at the end, not 9. You'll have to be
    * careful to only access bitmaps inside of their bounds.
    *
    * You can assume that you will not be given a radius less than 1.
    *
    * @param p The picture to be blurred.
    * @param radius The radius of the blurring box.
    * @return A blurred version of the original picture.
    */
   public static int[] avgcomp (Pixel[][] src, int x, int y, int radius, int w, int h){
	   
	   int csum1 = 0;
	   int cnumber1 = 0;
	   int csum2 = 0;
	   int cnumber2 = 0;
	   int csum3 = 0;
	   int cnumber3 = 0;
	   for (int r1 = -radius; r1 <= radius; r1++){
		  for (int r2 = -radius; r2 <= radius; r2++){
		      if (((x + r1) < w) && ((x + r1) > 0) && ((y + r2) < h) && ((y + r2) > 0)){
				  Pixel pix = src[x + r1][y + r2];
				  csum1 += pix.getRed();
				  cnumber1++;
				  csum2 += pix.getGreen();
				  cnumber2++;
				  csum3 += pix.getBlue();
				  cnumber3++;
			  }
		  }
	  }
	  int finalcolor1 = csum1/cnumber1;
	  int finalcolor2 = csum2/cnumber2;
	  int finalcolor3 = csum3/cnumber3;
	  int[] output = {finalcolor1, finalcolor2, finalcolor3};
	  return output;
	   
   }
   
   public static PictureObj blur(PictureObj p, int radius) {
	  
	  int h = p.getHeight();
	  int w = p.getWidth();
	  Pixel[][] src = p.getBitmap();
	  Pixel[][] tgt = new Pixel[w][h];
	  for (int x = 0; x < w; x++){
		  for (int y = 0; y < h; y++){
			  int[] newcomp = avgcomp (src, x, y, radius, w, h);
			  tgt[x][y] = new Pixel(newcomp);
		  }
	  }
	  return new PictureObj (tgt);
	
  }
   
}

