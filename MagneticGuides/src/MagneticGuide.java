
import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CSegment;
import fr.lri.swingstates.canvas.Canvas;



/**
 * @author AFRASS ILIAS
 * @author TAIBI ANAS
 *
 */

public class MagneticGuide extends CExtensionalTag {
	
	private CSegment cseg;
	private CExtensionalTag theTag;
	private int nb;
	private String type;
	

	public MagneticGuide(Canvas canvas, Point2D p, int nb, String type) {
		this.canvas = canvas;
		this.type = type;
		if(type.equals("horizontal")){
			this.cseg = this.canvas.newSegment(- canvas.getMaxX()*2, (int) p.getY(), canvas.getMaxX()*2, (int) p.getY());
		}
		else{
			this.cseg = this.canvas.newSegment((int) p.getX(), - canvas.getMaxY()*2, (int) p.getX(), canvas.getMaxY()*2);
		}
		this.cseg.setStroke(new BasicStroke(5));
		this.cseg.belowAll();
		this.cseg.addTag(this);
		this.theTag = this;
		this.cseg.addTo(canvas);
		this.nb = nb;
	}
	
	public CSegment getSeg(){
		return this.cseg;
	}

	public CExtensionalTag getTag(){
		return this.theTag;
	}
	
	public int getNb(){
		return this.nb;
	}
	
	public String getType(){
		return this.type;
	}
	
}