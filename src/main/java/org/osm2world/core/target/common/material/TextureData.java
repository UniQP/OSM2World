package org.osm2world.core.target.common.material;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import jakarta.xml.bind.DatatypeConverter;

/**
 * a texture with metadata necessary for calculating texture coordinates.
 */
public abstract class TextureData {

	public static enum Wrap { REPEAT, CLAMP, CLAMP_TO_BORDER }

	/** width of a single tile of the texture in meters, greater than 0 */
	public final double width;

	/** height of a single tile of the texture in meters, greater than 0 */
	public final double height;

	/**
	 * for textures that contain distinct, repeating objects (e.g. tiles), this describes the width of one such object.
	 * Some calculations for texture coords will use this to fit an integer number of such objects onto a surface.
	 */
	public final @Nullable Double widthPerEntity;

	/** see {@link #widthPerEntity} */
	public final @Nullable Double heightPerEntity;

	/** wrap style of the texture */
	public final Wrap wrap;

	/** calculation rule for texture coordinates */
	public final TexCoordFunction coordFunction;

	public TextureData(double width, double height, @Nullable Double widthPerEntity, @Nullable Double heightPerEntity,
			Wrap wrap, TexCoordFunction texCoordFunction) {

		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Illegal texture dimensions. Width: " + width + ", height: " + height);
		} else if (widthPerEntity != null && widthPerEntity <= 0 || heightPerEntity != null && heightPerEntity <= 0) {
			throw new IllegalArgumentException("Illegal per-entity texture dimensions.");
		}

		this.width = width;
		this.height = height;
		this.widthPerEntity = widthPerEntity;
		this.heightPerEntity = heightPerEntity;
		this.wrap = wrap;
		this.coordFunction = texCoordFunction;

	}

	/**
	 * returns the texture as a raster image file (png or jpeg).
	 * If the texture is originally defined as a procedural texture or vector graphics, a temporary file is provided.
	 */
	public abstract File getRasterImage();

	/**
	 * returns the texture as a {@link BufferedImage}.
	 * Like {@link #getRasterImage()}, this may involve converting a vector or procedural texture into a raster image.
	 */
	public abstract BufferedImage getBufferedImage();

	/**
	 * returns the texture as a data URI containing a raster image.
	 */
	public abstract String getDataUri();

	protected static final String imageToDataUri(BufferedImage image, String format) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    ImageIO.write(image, format, byteArrayOutputStream);
		    return "data:image/" + format + ";base64,"
		    		+ DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
		    throw new Error(e);
		}
	}

}
