package com.l2skale.multisell;

import java.util.List;

import javax.swing.JFrame;

import com.l2skale.multisell.data.MultisellSaver;
import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.ItemAmount;
import com.l2skale.multisell.model.MultisellList;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.utils.MessageUtils;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * @author Skache
 */
public class MultisellController
{
	private final IngredientList _ingredientList;
	private final ProductList _productList;
	private final MultisellList _multisellList;

	// Constructor to initialize the controller with the necessary models.
	public MultisellController(IngredientList ingredient, ProductList product, MultisellList multisellListModel)
	{
		this._ingredientList = ingredient;
		this._productList = product;
		this._multisellList = multisellListModel;
	}

	// Method to create a new item.
	public void createNewItem(JFrame frame)
	{
		if (_ingredientList.getIngredientsModel().isEmpty() || _productList.getProductsModel().isEmpty())
		{
			MessageUtils.showErrorMessage(frame, "You must add at least one ingredient and one product!", "Error");
			return;
		}

		List<ItemAmount> ingredients = _ingredientList.getIngredients();
		List<ItemAmount> products = _productList.getProducts();

		// Add the ingredients and products as a final product to the multisell list
		_multisellList.addAsFinalProduct(ingredients, products);

		Sound.playSound("quest_itemget.wav");
		// Show success message
		MessageUtils.showInfoMessage(frame, "New item has been added to the list!", "Success");

		// Clear the ingredient and product lists for the next item
		_ingredientList.getIngredientsModel().clear();
		_productList.getProductsModel().clear();
	}

	// Method to save the multisell list
	public void saveMultisell()
	{
		MultisellSaver multisellSaver = new MultisellSaver(_multisellList.getMultisellEntriesModel());
		multisellSaver.saveMultisell();
	}

	// Method to clear the lists
	public void clearLists()
	{
		_ingredientList.getIngredientsModel().clear();
		_productList.getProductsModel().clear();
	}
}
