package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WorldFileReader {

    private String filepath;

    private WorldEntity[][] entities;
    private Dimension dimension;

    public WorldEntity[][] getEntities() {
        return entities;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public WorldFileReader(String filepath) {
        //TODO change filepath (which had to be dynamically change by level number)
        this.filepath = filepath + "/level1.txt";

        try {//TODO what does if it throws an exception ?
            this.createWorldFromFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createWorldFromFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.filepath));

        String st = reader.readLine();
        int gridWidth = st.length();//TODO I thing it throws an exception if st is null
        int gridHeight = Math.toIntExact(reader.lines().count());
        System.out.println("========> height = " + gridHeight);
        System.out.println("========> width  = " + gridWidth);
        int line_counter = 0;

        this.entities = new WorldEntity[gridHeight][gridWidth];
        this.dimension = new Dimension(gridHeight, gridWidth);

        do {
            //compute one line of the grid
            for (int i = 0; i < gridWidth; i++){
                char actualChar = st.charAt(i);
                WorldEntity entity = WorldEntity.fromCode(actualChar).get();
                this.entities[line_counter][i] = entity;
//                System.out.print(this.entities[line_counter][i] + " / ");

//                System.out.print(entity);
            }
            System.out.println(st);

            line_counter++;

        } while ( (st = reader.readLine()) != null);

//        System.out.println("gridHeight = " + gridHeight + " | gridWidth = " + gridWidth);
//        System.out.println("-=-=-=-=-=-");
//        for(int i = 0; i < gridHeight; i++){
//            for(int j = 0; j < gridWidth; j++){
//                System.out.print(this.entities[i][j]);
//            }
//            System.out.println();
//        }

        reader.close();
    }
}
