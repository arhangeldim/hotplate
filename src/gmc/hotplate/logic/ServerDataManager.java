package gmc.hotplate.logic;

import gmc.hotplate.entities.Ingredient;
import gmc.hotplate.entities.Recipe;

import java.util.List;
import java.util.Map;

public class ServerDataManager implements IDataManager {

    @Override
    public List<Recipe> getRecipes(int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Ingredient> getProductsList(int type, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Ingredient, Float> getIngredients(int recipeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Recipe getRecipeById(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

}
