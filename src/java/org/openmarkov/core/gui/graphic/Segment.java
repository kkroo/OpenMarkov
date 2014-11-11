/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.geom.Point2D;


/**
 * This class represents a segment delimited by two points.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class Segment {

	/**
	 * Constant that defines normal segments (no vertical).
	 */
	public static final int SEGMENT_NORMAL = 1;

	/**
	 * Constant that defines vertical segments.
	 */
	public static final int SEGMENT_VERTICAL = 2;

	/**
	 * Type of the segment. Vertical segments are treated of different way than
	 * normal ones.
	 */
	private int segmentType;

	/**
	 * First limit of the segment.
	 */
	private Point2D.Double startPoint;

	/**
	 * Second limit of the segment.
	 */
	private Point2D.Double endPoint;

	/**
	 * Distance between both points;
	 */
	private double length;

	/**
	 * Constant 'm' in the ecuation 'y = mx + b'. Ignored in vertical lines.
	 */
	private double m;

	/**
	 * Constant 'b' in the ecuation 'y = mx + b'.
	 */
	private double b;

	/**
	 * Allowed constructor. It calculates the ecuation of the line that contains
	 * both points.
	 * 
	 * @param newStartPoint
	 *            first edge of the segment.
	 * @param newEndPoint
	 *            second edge of the segment.
	 * @throws IllegalArgumentException
	 *             if both points are equals.
	 */
	public Segment(Point2D.Double newStartPoint, Point2D.Double newEndPoint)
					throws IllegalArgumentException {

		startPoint = newStartPoint;
		endPoint = newEndPoint;
		if (newStartPoint.getX() == newEndPoint.getX()) {
			if (newStartPoint.getY() == newEndPoint.getY()) {
				throw new IllegalArgumentException();
			}
			segmentType = SEGMENT_VERTICAL;
			m = 0;
			b = newStartPoint.getX();
		} else {
			segmentType = SEGMENT_NORMAL;
			m =
				(newEndPoint.getY() - newStartPoint.getY())
					/ (newEndPoint.getX() - newStartPoint.getX());
			b = newStartPoint.getY() - m * newStartPoint.getX();
		}
		length = newStartPoint.distance(newEndPoint);

	}

	/**
	 * Returns the first limit of the segment.
	 * 
	 * @return the first limit of the segment.
	 */
	public Point2D.Double getStartPoint() {

		return startPoint;

	}

	/**
	 * Returns the second limit of the segment.
	 * 
	 * @return the second limit of the segment.
	 */
	public Point2D.Double getEndPoint() {

		return endPoint;

	}

	/**
	 * Returns the value of the constant 'm' in the expression 'y = mx + b'.
	 * 
	 * @return the value of 'm'.
	 */
	public double getM() {

		return m;

	}

	/**
	 * Returns the value of the constant 'b' in the expression 'y = mx + b'.
	 * 
	 * @return the value of 'b'.
	 */
	public double getB() {

		return b;

	}

	/**
	 * Returns the type of the segment.
	 * 
	 * @return segment type.
	 */
	public int getSegmentType() {

		return segmentType;

	}

	/**
	 * Returns the point where this segment and the parameter cut themselves.
	 * Both segments are normal.
	 * 
	 * @param segment
	 *            segment that cuts this one.
	 * @return a point where both segments have jointly or null if they have
	 *         not.
	 */
	private Point2D.Double cutPointNormalNormal(Segment segment) {

		double resultX;
		double resultY;
		Point2D.Double result;

		if (m != segment.getM()) {
			resultX = (segment.getB() - b) / (m - segment.getM());
			resultY = m * resultX + b;
			result = new Point2D.Double(resultX, resultY);

			return (insideSegment(result) && segment.insideSegment(result))
				? result : null;
		}

		return null;

	}

	/**
	 * Returns the point where this segment and the parameter cut themselves.
	 * The segment passed as parameter is vertical.
	 * 
	 * @param segment
	 *            segment that cuts this one.
	 * @return a point where both segments have jointly or null if they have
	 *         not.
	 */
	private Point2D.Double cutPointNormalVertical(Segment segment) {

		double resultX = segment.getB();
		double resultY = m * resultX + b;
		Point2D.Double result = new Point2D.Double(resultX, resultY);

		return (insideSegment(result) && segment.insideSegment(result))
			? result : null;

	}

	/**
	 * Returns the point where this segment and the parameter cut themselves.
	 * 
	 * @param segment
	 *            segment that cuts this one.
	 * @return a point that both segments have jointly or null if they have not.
	 */
	public Point2D.Double cutPoint(Segment segment) {

		if (segmentType == SEGMENT_NORMAL) {

			return (segment.getSegmentType() == SEGMENT_NORMAL)
				? cutPointNormalNormal(segment)
				: cutPointNormalVertical(segment);
		} else if (segment.getSegmentType() == SEGMENT_NORMAL) {
			return segment.cutPoint(this);
		} else {
			return null;
		}

	}

	/**
	 * Returns the point(s) where a segment and a circle cut themselves. The
	 * segment is horizontal.
	 * 
	 * @param segment
	 *            horizontal segment that cuts the circle.
	 * @param circleRadius
	 *            radius of the circle that cuts this segment.
	 * @return an array which contains the points that the circle and the
	 *         segment have jointly or null if they haven't.
	 */
	private Point2D.Double[] cutPointVertical(Segment segment, double circleRadius) {

		double r2 = Math.pow(circleRadius, 2);
		double b2 = Math.pow(segment.getB(), 2);
		double y = Math.sqrt(r2 - b2);
		Point2D.Double point1 = null, point2 = null;

		if (y == Double.NaN) {

			return null;
		}
		point1 = new Point2D.Double(segment.getB(), y);
		if (!segment.insideSegment(point1)) {
			point1 = null;
		}
		point2 = new Point2D.Double(segment.getB(), -y);
		if (!segment.insideSegment(point2)) {
			point2 = null;
		}
		if (point1 != null) {

			return (point2 != null) ? new Point2D.Double[] { point1, point2 }
				: new Point2D.Double[] { point1 };
		}

		return (point2 != null) ? new Point2D.Double[] { point2 } : null;

	}

	/**
	 * Returns the point(s) where this segment and the circle cut themselves.
	 * The segment is horizontal.
	 * 
	 * @param segment
	 *            vertical segment that cuts the circle.
	 * @param circleRadius
	 *            radius of the circle that cuts this segment.
	 * @return an array which contains the points that the circle and the
	 *         segment have jointly or null if they haven't.
	 */
	private Point2D.Double[] cutPointHorizontal(Segment segment, double circleRadius) {

		double r2 = Math.pow(circleRadius, 2);
		double segmentM = segment.getM();
		double segmentM2 = segmentM * segmentM;
		double segmentB = segment.getB();
		double segmentB2 = segmentB * segmentB;
		double segmentM2plus1 = segmentM2 + 1;
		double segmentMsegmentB = segmentM * segmentB;
		double squareroot = Math.sqrt(segmentM2plus1 * r2 - segmentB2);
		double x;
		Point2D.Double point1 = null, point2 = null;

		if (squareroot == Double.NaN) {

			return null;
		}
		x = (-segmentMsegmentB + squareroot) / segmentM2plus1;
		point1 = new Point2D.Double(x, segmentM * x + segmentB);
		if (!segment.insideSegment(point1)) {
			point1 = null;
		}
		x = (-segmentMsegmentB - squareroot) / segmentM2plus1;
		point2 = new Point2D.Double(x, segmentM * x + segmentB);
		if (!segment.insideSegment(point2)) {
			point2 = null;
		}
		if (point1 != null) {

			return (point2 != null) ? new Point2D.Double[] { point1, point2 }
				: new Point2D.Double[] { point1 };
		}

		return (point2 != null) ? new Point2D.Double[] { point2 } : null;

	}

	/**
	 * Returns the point(s) where this segment and the circle cut themselves.
	 * 
	 * @param circleCenter
	 *            center of the circle that cuts this segment.
	 * @param circleRadius
	 *            radius of the circle that cuts this segment.
	 * @return an array which contains the points that the circle and the
	 *         segment have jointly or null if they haven't.
	 */
	public Point2D.Double[] cutPoint(Point2D.Double circleCenter, double circleRadius) {

		double cx = circleCenter.getX();
		double cy = circleCenter.getY();
		Segment segment;
		Point2D.Double[] points;

		try {
			segment =
				new Segment(new Point2D.Double(startPoint.getX() - cx,
					startPoint.getY() - cy), new Point2D.Double(endPoint.getX()
					- cx, endPoint.getY() - cy));
		} catch (IllegalArgumentException e) {

			return null;
		}
		if (segment.getSegmentType() == SEGMENT_NORMAL) {
			points = cutPointHorizontal(segment, circleRadius);
		} else {
			points = cutPointVertical(segment, circleRadius);
		}
		if (points != null) {
			for (Point2D.Double point : points) {
				point.setLocation(point.getX() + cx, point.getY() + cy);
			}
		}

		return points;

	}

	/**
	 * This method determines if the point of the line that contains the segment
	 * is placed inside the segment.
	 * 
	 * @param point
	 *            point of the line.
	 * @return true if the point is placed inside the segment, false if not.
	 */
	private boolean insideSegment(Point2D.Double point) {

		double length1 = startPoint.distance(point);
		double length2 = endPoint.distance(point);

		return ((length1 <= length) && (length2 <= length));

	}

	@Override
	public String toString() {
		return startPoint + " -- " + endPoint;
	}
	
	
}
