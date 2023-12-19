package dominio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dominio.ImaJ.ImaJ;
import dominio.ImaJ.Properties;
import persistencia.ImageReader;
import visao.ImageShow;


public class Processor3 {

	public List<Entity> process(File file) {
		ImageShow imageShow = new ImageShow();
		double areaMaior = Double.NEGATIVE_INFINITY;
		int indexMaior=0;
		double percentIpe=0;
		double percentPauBrasil=0;
		double percentXanana=0;
		double percentRoseira=0;
		double circulaty=0;
		String classification="";
		
		
		ArrayList<Entity> list = new ArrayList<>();
		int[][][] im = ImageReader.imRead(file.getPath());
		//Image.imWrite(im, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/original.png");

		im = ImaJ._imResize(im);
		//int [][][] imResized = im;
		//Image.imWrite(im, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/reduzida.png");

		int[][][] im_blur = ImaJ.imGaussian(im, 5);
//		int [][] filter = {{-1, 0 ,1},
//							{-2, 0, 2},
//							{-1, 0, 1}};
//		
		//int[][] im_gray = ImaJ.rgb2gray(im_blur);

		int[][] im_red = ImaJ.splitChannel(im_blur, 0);
//		imageShow.imShow(im_red, file.getPath());
		//Image.imWrite(im_blur, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/blur.png");

		int[][] im_green = ImaJ.splitChannel(im_blur, 2);
//		imageShow.imShow(im_green, file.getPath());

		int[][] im_blue = ImaJ.splitChannel(im_blur, 1);
//		imageShow.imShow(im_blue, file.getPath());
		
		int[][][] im_cmyk = ImaJ.rgb2cmyk(im_blur);
		
		int[][] im_cyan = ImaJ.splitChannel(im_cmyk, 0);
//		imageShow.imShow(im_cyan, file.getPath(), "Ciano");
		//Image.imWrite(im_cyan, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/ciano.png");

		int[][] im_magenta = ImaJ.splitChannel(im_cmyk, 1);
		
//		imageShow.imShow(im_magenta, file.getPath(), "Magenta");
//		Image.imWrite(im_magenta, "C:\\Users\\vitin\\Documents\\TADS\\Banco de imagens - Projeto PDI\\Results\\magenta.png");

		int[][] im_yellow = ImaJ.splitChannel(im_cmyk, 2);
//		imageShow.imShow(im_yellow, file.getPath(), "Amarelo");
		//Image.imWrite(im_yellow, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/amarelo.png");

		int[][] im_black = ImaJ.splitChannel(im_cmyk, 3);
//		imageShow.imShow(im_black, file.getPath(), "Preto");
		//Image.imWrite(im_black, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/preto.png");
//		int [][] im_magenta2 = im_magenta;
//		int [][] imBorda1 = ImaJ.imfilter(im_black, filter);
//		int [][] imBorda2 = ImaJ.imfilter(im_yellow, filter);
//		int [][] imBorda3 = ImaJ.imfilter(im_magenta2, filter);
//		
//		boolean[][] mask1 = ImaJ.im2bw(imBorda1,30);
//		boolean[][] mask2 = ImaJ.im2bw(imBorda2,30);
//		boolean[][] mask3 = ImaJ.im2bw(imBorda3,30);
//		imageShow.imShow(mask1,file.getPath());
//		imageShow.imShow(mask2,file.getPath());
//		imageShow.imShow(mask3,file.getPath());
//		boolean[][] mask;
		
		boolean[][] im_mask = ImaJ.im2bw(im_magenta);
		boolean[][] imBordas = new boolean[im_mask.length][im_mask[0].length];
		
		
		//Image.imWrite(im_mask, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/mascara.png");

		boolean[][] imErode = ImaJ.bwErode(im_mask, 5);
		boolean[][] imErode2 = ImaJ.bwErode(im_mask, 2);
		
		for(int i=0; i < im_mask.length; i++) {
			for(int j=0; j < im_mask[0].length;j++) {
				imBordas[i][j] = im_mask[i][j] && !(imErode2[i][j]);
			}
		}
//		imageShow.imShow(imBordas, file.getPath());

		//boolean[][] imDilate = ImaJ.bwDilate(imErode, 3);
		//imageShow.imShow(imErode, file.getPath());
		//Image.imWrite(imErode, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/erodida.png");
		int area = im.length * im[0].length;
		
		System.out.println(area);

		double qtdIpe;
		ArrayList<Properties> sementes = ImaJ.regionProps(imErode);
		ArrayList<Properties> sementesSemPauBrasil = ImaJ.regionProps(imErode);
		int perimetro;
		
		for(int i = 0; i < sementes.size(); i++) {
			if(sementes.get(i).area > 350) {
				int[][][] im2 = ImaJ.imCrop(im, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1], 
                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);
				
				if(areaMaior < sementes.get(i).area) {
					areaMaior = sementes.get(i).area;
					indexMaior = i;
				}
			}
		}
		
		qtdIpe=0;
		for(int i = 0; i < sementes.size(); i++) {
			perimetro=0;
			if(sementes.get(i).area > 350) {
				int[][][] im2 = ImaJ.imCrop(im, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1], 
                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);
				
				boolean[][] imMedian = ImaJ.imCrop(imBordas, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1], 
                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);
							
						// Aplicando máscara na imagem original
						for(int x = 0; x < im2.length; x++) {
							for(int y = 0; y < im2[0].length; y++) {
								//Se é pixel de fundo
								if(!sementes.get(i).image[x][y]) {
									im2[x][y] = new int[]{0,0,0};
								}
									perimetro += imMedian[x][y] ? 1 : 0;
								
							}
						}

//						System.out.println("Area maior: " + list.get(i).getArea());
//						System.out.println("Area do pau brasil: "+list.get(indexMaior).getArea());
//						System.out.println(list.get(i).getArea()/list.get(indexMaior).getArea());
						qtdIpe = (double)sementes.get(i).area/(double)sementes.get(indexMaior).area;
						
						if(qtdIpe < 0.07 ) {
							classification = "Pau Brasil";
						}else if(qtdIpe <= 0.2) {
							classification = "Xanana";
						}else if(qtdIpe >= 0.3 && qtdIpe < 1) {
							classification = "Roseira";
						}else {
							classification = "Ipe";
						}
						percentIpe = Math.sqrt(Math.pow((187965-sementes.get(i).area),2));
						percentPauBrasil = Math.sqrt(Math.pow((11520-sementes.get(i).area),2));
						percentXanana = Math.sqrt(Math.pow((20918-sementes.get(i).area),2));
						percentRoseira = Math.sqrt(Math.pow((158004-sementes.get(i).area),2));
		
						circulaty =  (4* 3.14 * sementes.get(i).area)/(perimetro*perimetro); 
						
						ImageReader.imWrite(im2, file.getPath().split("\\.")[0] + "_" + i+ classification+ ".png");
//						System.out.println(sementes.get(i).image.length * sementes.get(i).image[0].length);
						//ImageReader.imWrite(im2,"C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/folha_" + i + ".png");
						list.add(new Entity(sementes.get(i).area, percentIpe, percentXanana, percentPauBrasil, percentRoseira, circulaty, qtdIpe, file.getPath().split("\\.")[0] + "_" + i +classification+ ".png", classification));			
						}
			}	
		
//		for(int i = 0; i < list.size(); i++) {
////			if(areaMaior != sementes.get(i).area) {
//				list.get(i).setPauBrasilQtd(list.get(i).getArea()/list.get(indexMaior).getArea());
//				System.out.println("Area maior: " + list.get(i).getArea());
//				System.out.println("Area do pau brasil: "+list.get(indexMaior).getArea());
//				System.out.println(list.get(i).getArea()/list.get(indexMaior).getArea());
//				qtdIpe = list.get(i).getArea()/list.get(indexMaior).getArea();
//				
//				if(qtdIpe < 0.07 ) {
//					classification = "Pau Brasil";
//				}else if(qtdIpe <= 0.2) {
//					classification = "Xanana";
//				}else if(qtdIpe >= 0.3 && qtdIpe < 1) {
//					classification = "Roseira";
//				}else {
//					classification = "Ipe";
//				}
//				list.get(i).setClassification(classification);
////			}else {
////				list.get(i).setClassification("Pau Brasil");
////			}
//		}
		
		
		return list;
	}
}
