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

public class Processor2 {

	public List<Entity> process(File file) {
		ImageShow imageShow = new ImageShow();

		double percentIpe;
		double percentPauBrasil;
		double percentXanana;
		double percentRoseira;
		String classification;
		
		ArrayList<Entity> list = new ArrayList<>();
		int[][][] im = ImageReader.imRead(file.getPath());
		//Image.imWrite(im, "C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/original.png");

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
//		imageShow.imShow(im_magenta, file.getPath(), "Magenta");
//		Image.imWrite(im_magenta, "C:\\Users\\vitin\\Documents\\TADS\\Banco de imagens - Projeto PDI\\Results\\magenta.png");

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
//						percentIpe = 187965/(double) sementes.get(i).area ;
//						percentPauBrasil = 11520/(double)sementes.get(i).area;
//						percentXanana = 20918/(double)sementes.get(i).area;
//						percentRoseira = 158004/(double)sementes.get(i).area;

						percentIpe = Math.sqrt(Math.pow((187965-sementes.get(i).area),2));
						percentPauBrasil = Math.sqrt(Math.pow((11520-sementes.get(i).area),2));
						percentXanana = Math.sqrt(Math.pow((20918-sementes.get(i).area),2));
						percentRoseira = Math.sqrt(Math.pow((158004-sementes.get(i).area),2));
						classification = "desconhecido";
						
						if(percentIpe < percentPauBrasil && percentIpe < percentXanana && percentIpe < percentRoseira)
							classification = "Ipe";
						if (percentPauBrasil < percentIpe && percentPauBrasil < percentXanana && percentPauBrasil < percentRoseira)
							classification = "Pau Brasil";
						if (percentXanana < percentPauBrasil && percentXanana < percentIpe && percentXanana < percentRoseira)
							classification = "Xanana";
						if (percentRoseira < percentPauBrasil && percentRoseira < percentIpe && percentRoseira < percentXanana)
							classification = "Roseira";
//						if(percentIpe < 1.3) {
//							System.out.println(percentIpe);
//							if(percentIpe > percentPauBrasil && percentIpe > percentXanana && percentIpe > percentRoseira)
//								classification = "Ipe";
//						}
//						
//						if(percentXanana < 1.3) {
//							System.out.println(percentIpe);
//							if(percentXanana > percentPauBrasil && percentXanana > percentRoseira)
//								classification = "Xanana";
//						}
						ImageReader.imWrite(im2, file.getPath().split("\\.")[0] + "_" + classification + ".png");
						//ImageReader.imWrite(im2,"C:/Users/vitin/Documents/TADS/PDI/ProjetoPDI/Results/folha_" + i + ".png");
						list.add(new Entity(sementes.get(i).area, percentIpe, percentXanana, percentPauBrasil, percentRoseira, file.getPath().split("\\.")[0] + "_" + classification + ".png", classification));			
						}
			}	
		
		return list;
	}
}
