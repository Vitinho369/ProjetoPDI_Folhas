package dominio;

public class Entity {
	private double area;
	private double percentIpe;
	private double percentPauBrasil;
	private double percentXanana;
	private double percentRoseira;
	private double qtdIpe;
	private double circulaty;
	private String classification;
	private String image;
	
	public Entity(double area, double color, String image, String classification) {
		this.area = area;
		this.setImage(image);
		this.setClassification(classification);
	}
	
	public Entity(double area, double percentIpe,double percentXanana,double percentPauBrasil, double percentRoseira,double circulaty, double qtdIpe, String image, String classification) {
		this.area = area;
		this.setImage(image);
		this.percentIpe = percentIpe;
		this.percentXanana = percentXanana;
		this.percentPauBrasil = percentPauBrasil;
		this.percentRoseira = percentRoseira;
		this.setClassification(classification);
		this.circulaty = circulaty;
		this.qtdIpe = qtdIpe;
	}
	

	public double getQtdIpe() {
		return qtdIpe;
	}

	public void setQtdIpe(double qtdIpe) {
		this.qtdIpe = qtdIpe;
	}

	public double getCirculaty() {
		return circulaty;
	}

	public void setCirculaty(double circulaty) {
		this.circulaty = circulaty;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getPercentIpe() {
		return percentIpe;
	}

	public void setPercentIpe(double percentIpe) {
		this.percentIpe = percentIpe;
	}

	public double getPercentPauBrasil() {
		return percentPauBrasil;
	}

	public void setPercentPauBrasil(double percentPauBrasil) {
		this.percentPauBrasil = percentPauBrasil;
	}

	public double getPercentXanana() {
		return percentXanana;
	}

	public void setPercentXanana(double percentXanana) {
		this.percentXanana = percentXanana;
	}

	public double getPercentRoseira() {
		return percentRoseira;
	}

	public void setPercentRoseira(double percentRoseira) {
		this.percentRoseira = percentRoseira;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
}
