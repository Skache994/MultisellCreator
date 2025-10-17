package com.l2skale.multisell.ui.dnd;

import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import com.l2skale.multisell.enums.ItemType;
import com.l2skale.multisell.model.IngredientList;
import com.l2skale.multisell.model.Item;
import com.l2skale.multisell.model.ProductList;
import com.l2skale.multisell.ui.panels.popup.DialogUtils;

/*
 * @author Skache
 */
public class ItemImportTransferHandler extends TransferHandler
{
	private static final long serialVersionUID = 1L;

	private final IngredientList _ingredientList;
	private final ProductList _productList;
	private final ItemType _dropType;

	public ItemImportTransferHandler(IngredientList ingredient, ProductList product, ItemType dropType)
	{
		_ingredientList = ingredient;
		_productList = product;
		_dropType = dropType;
	}

	@Override
	public boolean canImport(TransferSupport support)
	{
		return support.isDataFlavorSupported(ItemTransferable.ITEM_FLAVOR);
	}

	@Override
	public boolean importData(TransferSupport support)
	{
		if (!canImport(support))
			return false;

		try
		{
			Item item = (Item) support.getTransferable().getTransferData(ItemTransferable.ITEM_FLAVOR);
			Integer amount = DialogUtils.promptForAmount(SwingUtilities.getWindowAncestor(support.getComponent()), item.getName());

			if (amount != null)
			{
				switch (_dropType)
				{
					case INGREDIENT:
					{
						_ingredientList.addAsIngredient(item, amount);
					}
						break;
					case PRODUCT:
					{
						_productList.addAsProduct(item, amount);
					}
						break;
					default:
						System.err.println("Unsupported drop type: " + _dropType);
						return false;
				}
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}