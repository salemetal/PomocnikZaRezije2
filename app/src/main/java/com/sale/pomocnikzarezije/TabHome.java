package com.sale.pomocnikzarezije;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sale.pomocnikzarezije.db.DBHandler;

import java.util.ArrayList;

/**
 * Created by Sale on 19.10.2016..
 */
public class TabHome extends Fragment {

    private ArrayList<Category> categoryList = new ArrayList<>();
    int longClickedItemIndex;
    private static final int EDIT = 0, DELETE = 1;
    private DBHandler dbHandler;
    private String newName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_home, container, false);

        dbHandler = new DBHandler(this.getContext());
        categoryList = dbHandler.getAllCategories();

        if (categoryList.isEmpty())
        {
            LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.llTabHome);
            TextView textView = new TextView(this.getContext());
            textView.setText(R.string.welcome_message);
            textView.setPadding(20,20,0,0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            layout.addView(textView);

        }

        else
        {
            ListView lv = (ListView) rootView.findViewById(R.id.listViewCategories);

            AdapterCategory adapter = new AdapterCategory(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    categoryList);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    Category item = categoryList.get(position);

                    Intent intent = new Intent(getActivity(), AddEditRezija.class);
                    intent.putExtra("item_name", item.getName());
                    intent.putExtra("item_id", item.getId());
                    startActivity(intent);
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    longClickedItemIndex = position;
                    return false;
                }
            });

            lv.setAdapter(adapter);
            registerForContextMenu(lv);
        }

        FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab_add_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.naziv_kategorije);

                final EditText editText = new EditText(getContext());
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                int maxLength = Utils.categoryMaxlength;
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                builder.setView(editText);

                builder.setPositiveButton(R.string.spremi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        newName = editText.getText().toString().trim().toUpperCase();
                        if (newName.isEmpty()) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                            builder2.setTitle(R.string.upozorenje);
                            builder2.setMessage(R.string.naz_kat_prazan);
                            builder2.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            }).show();
                        } else {
                            if (categoryList.toString().contains(newName)) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                                builder2.setTitle(R.string.upozorenje);
                                builder2.setMessage(String.format("Kategorija %s već postoji! Odaberite drugi naziv.", newName));
                                builder2.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).show();
                            } else {
                                try {
                                    dbHandler.addCategory(new Category(newName));
                                    Toast.makeText(view.getContext(), R.string.spremljeno, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    throw ex;
                                }
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.odustani, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                }).show();
            }
        });

        return rootView;
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(Menu.NONE, EDIT, menu.NONE, R.string.uredi);
        menu.add(Menu.NONE, DELETE, menu.NONE, R.string.obrisi);
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (getUserVisibleHint())
        {
            Category category = categoryList.get(longClickedItemIndex);

            switch (item.getItemId()) {
                case EDIT:
                    confirmDialogEditCategory(category, dbHandler, this.getView());
                    break;
                case DELETE:
                    confirmDialogDeleteCategory(category, dbHandler, this.getView());
                    break;
            }
            return super.onContextItemSelected(item);
        }
        return false;
    }

    private void confirmDialogDeleteCategory(final Category category, final DBHandler dbHandler, final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

        try {
            builder
                    .setMessage(String.format("Jeste li sigurni da želite obrisati kategoriju %s? Brišu se i svi podaci vezani za nju!", category.getName()))
                    .setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dbHandler.deleteCategory(category);

                            Toast.makeText(v.getContext(), R.string.obrisano, Toast.LENGTH_SHORT)
                                    .show();

                            Intent intent = new Intent(getView().getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.ne, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void confirmDialogEditCategory(final Category category, final DBHandler dbHandler, final View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.pr_ime_kat);

        final EditText editText = new EditText(this.getContext());
        editText.setText(category.getName());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(editText);

        builder.setPositiveButton(R.string.spremi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                newName = editText.getText().toString().trim().toUpperCase();

                if (newName.isEmpty()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                    builder2.setTitle(R.string.upozorenje);
                    builder2.setMessage(R.string.naz_kat_prazan);
                    builder2.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
                } else {
                    try {
                        category.setName(newName);
                        dbHandler.updateCategory(category);
                        Toast.makeText(v.getContext(), R.string.azurirano, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getView().getContext(), MainActivity.class);
                        startActivity(intent);

                    } catch (Exception ex) {
                        throw ex;
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.odustani, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();
    }
}
