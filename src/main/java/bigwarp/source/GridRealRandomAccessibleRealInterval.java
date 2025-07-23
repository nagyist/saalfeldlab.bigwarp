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
package bigwarp.source;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.realtransform.RealTransform;
import net.imglib2.type.numeric.RealType;

public class GridRealRandomAccessibleRealInterval<T extends RealType<T>> extends AbstractRealInterval implements RealRandomAccessibleRealInterval<T> 
{
	GridRealRandomAccess<T> ra;
	
	public GridRealRandomAccessibleRealInterval( Interval interval, T t, RealTransform warp )
	{
		super( interval );
		ra = new GridRealRandomAccess< T >( new double[ interval.numDimensions() ], t, warp );
	}

	@Override
	public RealRandomAccess<T> realRandomAccess() {
		return ra.copy();
	}

	@Override
	public RealRandomAccess<T> realRandomAccess(RealInterval interval) {
		return realRandomAccess();
	}

	@Override
	public T getType()
	{
		return ra.getType();
	}

}
