package dominio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dominio.ImaJ.ImaJ;
import dominio.ImaJ.Image;
import dominio.ImaJ.Properties;
import persistencia.ImageReader;
import visao.ImageShow;
import java.util.Arrays;
import static java.lang.Double.POSITIVE_INFINITY;

public class Processor {

	public List<Entity> process(File file) {
		ImageShow imageShow = new ImageShow();
		
		ArrayList<Entity> list = new ArrayList<>();
		int[][][] im = ImageReader.imRead(file.getPath());
		//Image.imWrite(im, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/original.png");

		ArrayList<String> classifications = new ArrayList<String>(); 
		ArrayList<Double> areas = new ArrayList<Double>(); 
		
		double areaMaior = Double.NEGATIVE_INFINITY, areaMenor = Double.POSITIVE_INFINITY;
		im = ImaJ._imResize(im);
		//int [][][] imResized = im;
		//Image.imWrite(im, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/reduzida.png");

		int[][][] im_blur = ImaJ.imGaussian(im, 5);
	
		//int[][] im_gray = ImaJ.rgb2gray(im_blur);


		//int[][] im_red = ImaJ.splitChannel(im_blur, 0);
		//imageShow.imShow(im_red, file.getPath());
		//Image.imWrite(im_blur, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/blur.png");

		//int[][] im_green = ImaJ.splitChannel(im_blur, 2);
		//imageShow.imShow(im_green, file.getPath());

		//int[][] im_blue = ImaJ.splitChannel(im_blur, 1);
		//imageShow.imShow(im_blue, file.getPath());
		
		int[][][] im_cmyk = ImaJ.rgb2cmyk(im_blur);
		
		int[][] im_cyan = ImaJ.splitChannel(im_cmyk, 0);
		//imageShow.imShow(im_cyan, file.getPath(), "Ciano");
		//Image.imWrite(im_cyan, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/ciano.png");

		int[][] im_magenta = ImaJ.splitChannel(im_cmyk, 1);
		imageShow.imShow(im_magenta, file.getPath(), "Magenta");
		Image.imWrite(im_magenta, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/magenta.png");

		int[][] im_yellow = ImaJ.splitChannel(im_cmyk, 2);
		//imageShow.imShow(im_yellow, file.getPath(), "Amarelo");
		//Image.imWrite(im_yellow, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/amarelo.png");

		int[][] im_black = ImaJ.splitChannel(im_cmyk, 3);
		//imageShow.imShow(im_black, file.getPath(), "Preto");
		//Image.imWrite(im_black, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/preto.png");

		
		boolean[][] im_mask = ImaJ.im2bw(im_magenta);
		//Image.imWrite(im_mask, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/mascara.png");

		boolean[][] imErode = ImaJ.bwErode(im_mask, 5);
		//boolean[][] imDilate = ImaJ.bwDilate(imErode, 3);
		//imageShow.imShow(imErode, file.getPath());
		//Image.imWrite(imErode, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/erodida.png");

		ArrayList<Properties> sementes = ImaJ.regionProps(imErode);

		for(int i = 0; i < sementes.size(); i++) {
			if(sementes.get(i).area > 350) {
				int[][][] im2 = ImaJ.imCrop(im, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1], 
                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);
						
						// Aplicando máscara na imagem original
						for(int x = 0; x < im2.length; x++) {
							for(int y = 0; y < im2[0].length; y++) {
								//Se é pixel de fundo
								if(!sementes.get(i).image[x][y]) {
									im2[x][y] = new int[]{0,0,0};
								}
							}
						}
						//ImageReader.imWrite(im2, file.getPath().split("\\.")[0] + "_" + i + ".png");
						//ImageReader.imWrite(im2,"C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/folha_" + i + ".png");
						list.add(new Entity(sementes.get(i).area, 1, file.getPath().split("\\.")[0] + "_" + i + ".png", "desconhecido ainda"));			
						}
				
			}	
		
		for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getArea() > areaMaior) {
					areaMaior = sementes.get(i).area;
						if(classifications.contains("Ipe")) {
							int index = classifications.indexOf("Ipe");
							classifications.set(index,"desconhecido ainda");
							areas.set(index, 0.0);
						}
						
						classifications.add("Ipe");
						areas.add(areaMaior);
				}
				
				if(list.get(i).getArea() < areaMenor) {
					areaMenor = list.get(i).getArea();
						if(classifications.contains("Pau Brasil")) {
							int index = classifications.indexOf("Pau Brasil");
							classifications.set(index,"desconhecido ainda");
							areas.set(index, 0.0);
						}
						
						classifications.add("Pau Brasil");
						areas.add(areaMenor);
			}
				System.out.println(list.get(i).getArea());
		}
		
		
		System.out.println(classifications.toString());
		System.out.println(areas.toString());
		for(int i = 0; i < list.size(); i++) {
			for(int j=0; j < areas.size();j++) {
				if(list.get(i).getArea() == areas.get(j)) {
					list.get(i).setClassification(classifications.get(j));
				}
			}
		}
		areaMaior = Double.NEGATIVE_INFINITY;
		areaMenor = Double.POSITIVE_INFINITY;
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getClassification().equals("desconhecido ainda")) {
				
				if(list.get(i).getArea() > areaMaior) {
					areaMaior = sementes.get(i).area;
						if(classifications.contains("Roseira")) {
							int index = classifications.indexOf("Roseira");
							classifications.set(index,"desconhecido ainda");
							areas.set(index, 0.0);
						}
						
						classifications.add("Roseira");
						areas.add(areaMaior);
				}
				
				if(list.get(i).getArea() < areaMenor) {
					areaMenor = list.get(i).getArea();
						if(classifications.contains("Xanana")) {
							int index = classifications.indexOf("Xanana");
							classifications.set(index,"desconhecido ainda");
							areas.set(index, 0.0);
						}
						
						classifications.add("Xanana");
						areas.add(areaMenor);
				}
				
			}
		}
		
		for(int i = 0; i < list.size(); i++) {
			for(int j=0; j < areas.size();j++) {
				if(list.get(i).getArea() == areas.get(j)) {
					list.get(i).setClassification(classifications.get(j));
				}
			}
		}
		return list;
	}
}