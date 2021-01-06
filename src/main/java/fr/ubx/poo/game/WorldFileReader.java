package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read level's config file to setup World
 */
public class WorldFileReader {

    private String filepath;
    private WorldEntity[][] entities;
    private Dimension dimension;

    /**
     * Getter for entities
     *
     * @return world's entity matrix
     */
    public WorldEntity[][] getEntities() {
        return entities;
    }

    /**
     * Getter for wordl's dimension
     *
     * @return world's dimension
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Constructor
     *
     * @param filepath path to the level's config file
     */
    public WorldFileReader(String filepath) {
        //TODO change filepath (which had to be dynamically change by level number)
        this.filepath = filepath;

        try {//TODO what does if it throws an exception ?
            this.createWorldFromFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * To read the file in order to setup a new World
     *
     * @throws IOException If an I/O error occurs
     */
    private void createWorldFromFile() throws IOException {
        //Variables
        BufferedReader reader = new BufferedReader(new FileReader(this.filepath));

        String st = reader.readLine();
        int gridWidth = st.length();//TODO I thing it throws an exception if st is null
        int line_counter = 0;

        List<List<WorldEntity>> dynamicMatrix = new ArrayList<>();

        //loop to read the file
        do {
            //temporary list
            List<WorldEntity> tmpDynamicMatrix = new ArrayList<>();

            //compute one line of the grid
            for (int i = 0; i < gridWidth; i++){
                //get the actual entity
                char actualChar = st.charAt(i);
                WorldEntity entity = WorldEntity.fromCode(actualChar).get();
                //add actual entity to dynamicMatrix
                tmpDynamicMatrix.add(entity);
            }

            //TODO throw if size is incoherent

            //to count number of lines
            line_counter++;

            //complete dynamicMatrix
            dynamicMatrix.add(tmpDynamicMatrix);

        } while ( (st = reader.readLine()) != null);

        //intiate new tab for entities
        int gridHeight = dynamicMatrix.size();
        this.entities = new WorldEntity[gridHeight][gridWidth];

        //TODO throw if number of line is incoherent

        //initiate entities
        for(int i = 0; i < gridHeight; i++){
            for(int j = 0; j < gridWidth; j++){
                WorldEntity tmpEntity = dynamicMatrix.get(i).get(j);
                entities[i][j] = tmpEntity;
            }
        }

        //initiate dimension
        this.dimension = new Dimension(gridHeight, gridWidth);

        reader.close();
    }
}
