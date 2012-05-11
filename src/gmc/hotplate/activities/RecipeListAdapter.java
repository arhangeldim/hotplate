package gmc.hotplate.activities;

import gmc.hotplate.R;
import gmc.hotplate.entities.Recipe;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecipeListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Recipe> recipes;

    public RecipeListAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
        inflater = (LayoutInflater) this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recipe_list_item, parent, false);
        }
        Recipe recipe = (Recipe) getItem(position);
        ((TextView) convertView.findViewById(R.id.tvName)).setText(recipe.getName());
        ((TextView) convertView.findViewById(R.id.tvDescr)).setText(recipe.getDescription());
        return convertView;
    }

}
