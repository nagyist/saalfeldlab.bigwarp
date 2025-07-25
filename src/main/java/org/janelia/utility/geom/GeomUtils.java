/*-
 * #%L
 * BigWarp plugin for Fiji.
 * %%
 * Copyright (C) 2015 - 2025 Howard Hughes Medical Institute.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package org.janelia.utility.geom;

import java.util.List;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;

public class GeomUtils {

	/**
	 * Finds the parameters of the smallest hypersphere containing the points.
	 *
	 * @param pts a list of points
	 * @return a pair containing the center and the squared distance
	 */
	public static Pair<RealPoint,Double> smallestEnclosingSphere( List<RealPoint> pts )
	{
		RealPoint p = null;
		RealPoint q = null;
		double maxSqrDist = Double.POSITIVE_INFINITY;
		// find pair of points with the largest distance
		for( int i = 0; i < pts.size(); i++)
			for( int j = i+1; j < pts.size(); j++) {
				final double d = squaredDistance( pts.get( i ), pts.get( j ));
				if( d < maxSqrDist )
				{
					maxSqrDist = d;
					p = pts.get( i );
					q = pts.get( j );
				}
			}

		final RealPoint center = new RealPoint( p.numDimensions());
		for( int d = 0; d < p.numDimensions(); d++ )
		{
			center.setPosition(
					0.5 * p.getDoublePosition(d) + 0.5 * q.getDoublePosition(d),
					d );
		}
		return new ValuePair<RealPoint, Double>(center, maxSqrDist);
	}

	final public static void scale( final RealPoint p, final double scale )
	{
		for( int i = 0; i < p.numDimensions(); i++ )
			p.setPosition( p.getDoublePosition( i ) * scale , i);
	}

	final public static double squaredDistance( final RealLocalizable position1, final RealLocalizable position2 )
	{
		double dist = 0;

		final int n = position1.numDimensions();
		for ( int d = 0; d < n; ++d )
		{
			final double pos = position2.getDoublePosition( d ) - position1.getDoublePosition( d );

			dist += pos * pos;
		}

		return dist;
	}

	final public static double squaredDistance( final double[] position1, final double[] position2 )
	{
		double dist = 0;

		final int n = position1.length;
		for ( int d = 0; d < n; ++d )
		{
			final double pos = position2[d] - position1[d];
			dist += pos * pos;
		}

		return dist;
	}

	public static AffineTransform2D fromScalesAngle( double... p )
	{
		return fromScalesAngle( p[0], p[1], p[2] );
	}

	public static AffineTransform2D fromScalesAngle( double sx, double sy, double tht )
	{
		final AffineTransform2D t = new AffineTransform2D();
		final double cosTht = Math.cos( tht );
		final double sinTht = Math.sin( tht );
		t.set(	sx * cosTht, -sx * sinTht, 0.0,
				sy * sinTht,  sy * cosTht, 0.0 );

		return t;
	}

	/**
	 * Returns an array containing [sx, sy, tht], where
	 * sx : the x scale
	 * sy : the y scale
	 * tht : the angle
	 *
	 * @param t the transformations
	 * @return the scales and angle [sx, sy, tht]
	 */
	public static double[] scalesAngle( final AffineTransform2D t )
	{
		final double a = t.get( 0, 0 ); final double b = t.get( 0, 1 );
		final double c = t.get( 1, 0 ); final double d = t.get( 1, 1 );

		final double sa = a >= 0 ? 1 : -1;
		final double sd = d >= 0 ? 1 : -1;

		final double mab = Math.sqrt( a * a + b * b );
		final double mcd = Math.sqrt( c * c + d * d );

		final double sx = sa * mab;
		final double sy = sd * mcd;
		final double tht = Math.atan2( -b, a );
		return new double[] { sx, sy, tht };
	}

	/**
	 * Returns an array containing [sx, sy, tht], where
	 * sx : the x scale
	 * sy : the y scale
	 * tht : the angle
	 *
	 * @param t the transformations
	 * @param center the coordinates of the center of rotation
	 * @return the scales and angle [sx, sy, tht]
	 */
	public static double[] scalesAngle( final AffineTransform2D t, final double[] center )
	{
		// TODO why does the center arg do nothing?

		final double a = t.get( 0, 0 ); final double b = t.get( 0, 1 );
		final double c = t.get( 1, 0 ); final double d = t.get( 1, 1 );

		// don't allow flips
//		final double sa = a >= 0 ? 1 : -1;
//		final double sd = d >= 0 ? 1 : -1;
		final double sa = 1.0;
		final double sd = 1.0;

		final double mab = Math.sqrt( a * a + b * b );
		final double mcd = Math.sqrt( c * c + d * d );

		final double sx = sa * mab;
		final double sy = sd * mcd;
		final double tht = Math.atan2( -b, a );
		return new double[] { sx, sy, tht };

//		final double tht1 = Math.atan2( -b, a );
//		final double tht2 = Math.atan2( c, d );
//		System.out.println( "tht1 : " + tht1 );
//		System.out.println( "tht2 : " + tht2 );
//		return new double[] { sx, sy, tht1, tht2 };
	}

	public static double[] evals2d( AffineGet a )
	{
		final double m = trace2d( a ) / 2.0;
		final double p = det2d( a );
		final double d = Math.sqrt( m * m - p );
		return new double[] { m + d, m - d };
	}

	public static double det2d( AffineGet a ) {
		return 	a.get( 0, 0 ) * a.get( 1, 1 ) -
				a.get( 1, 0 ) * a.get( 0, 1 );
	}

	public static double trace2d( AffineGet a ) {
		return a.get( 0, 0 ) + a.get( 1, 1 );
	}

	public static AffineTransform2D centeredRotation( final double tht, final double[] c )
	{
		final AffineTransform2D t = new AffineTransform2D();
		t.translate( -c[ 0 ], -c[ 1 ] );
		t.rotate( tht );
		t.translate( c );
		return t;
	}

	public static AffineTransform2D centeredSimilarity( final double tht, final double scale, final double[] c )
	{
		final AffineTransform2D t = new AffineTransform2D();
		t.translate( -c[ 0 ], -c[ 1 ] );
		t.rotate( tht );
		t.scale( scale );
		t.translate( c );
		return t;
	}

}
