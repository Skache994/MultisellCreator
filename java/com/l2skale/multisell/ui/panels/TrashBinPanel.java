package com.l2skale.multisell.ui.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.l2skale.multisell.ui.dnd.LocalObjectTransferable;
import com.l2skale.multisell.ui.utils.ResourceIcons;
import com.l2skale.multisell.ui.utils.Sound;

/*
 * @author Skache
 */
public class TrashBinPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JLabel _trashBin;
	private ImageIcon _trashIcon;
	private ImageIcon _trashIconHover;
	private ImageIcon _trashIconDrag;

	private Consumer<Object> _onDelete;

	public TrashBinPanel()
	{
		// Load the icons using loadResourceIconsIcon method from ResourceIcons class
		_trashIcon = ResourceIcons.loadResourceIconsIcon("inventory_trash.png");
		_trashIconHover = ResourceIcons.loadResourceIconsIcon("inventory_trash_over.png");
		_trashIconDrag = ResourceIcons.loadResourceIconsIcon("inventory_trash_drag.png");

		// Check if any of the icons failed to load
		if (_trashIcon == null || _trashIconHover == null || _trashIconDrag == null)
		{
			System.err.println("One or more TrashBin icons failed to load.");
		}

		// Create JLabel for the trash bin
		_trashBin = new JLabel(_trashIcon);
		_trashBin.setHorizontalAlignment(JLabel.CENTER);
		_trashBin.setPreferredSize(new Dimension(36, 36));

		// Add MouseListener to change the icon on hover and drag
		_trashBin.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				_trashBin.setIcon(_trashIconHover);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				_trashBin.setIcon(_trashIcon);
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				_trashBin.setIcon(_trashIconDrag);
				Sound.playSound("click_01.wav");
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				_trashBin.setIcon(_trashIconHover);
			}
		});

		_trashBin.setToolTipText("Destruction");

		// Accept items/entries dragged here and hand them to the delete callback.
		new DropTarget(_trashBin, new DropTargetAdapter()
		{
			@Override
			public void dragEnter(DropTargetDragEvent event)
			{
				// An item is being dragged onto the bin - show the "open" icon.
				_trashBin.setIcon(_trashIconDrag);
			}

			@Override
			public void dragExit(DropTargetEvent event)
			{
				_trashBin.setIcon(_trashIcon);
			}

			@Override
			public void drop(DropTargetDropEvent event)
			{
				try
				{
					final Transferable transferable = event.getTransferable();
					if (transferable.isDataFlavorSupported(LocalObjectTransferable.FLAVOR))
					{
						event.acceptDrop(DnDConstants.ACTION_MOVE);
						final Object value = transferable.getTransferData(LocalObjectTransferable.FLAVOR);
						if (_onDelete != null)
						{
							_onDelete.accept(value);
						}
						Sound.playSound("trash_basket.wav");
						event.dropComplete(true);
					}
					else
					{
						event.rejectDrop();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					event.rejectDrop();
				}
				finally
				{
					_trashBin.setIcon(_trashIcon);
				}
			}
		});

		// Panel for Trash Bin
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(_trashBin);
	}

	// Called with the dragged object (an item or an entry) when something is dropped on the bin.
	public void setOnDelete(Consumer<Object> onDelete)
	{
		_onDelete = onDelete;
	}
}
