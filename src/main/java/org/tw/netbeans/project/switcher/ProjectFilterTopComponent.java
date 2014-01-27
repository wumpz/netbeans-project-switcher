/*
 * #%L
 * project.switcher
 * %%
 * Copyright (C) 2013 - 2014 Toben
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package org.tw.netbeans.project.switcher;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Exceptions;

@ConvertAsProperties(
		dtd = "-//org.tw.netbeans.project.switcher//ProjectFilter//EN",
		autostore = false)
@TopComponent.Description(
		preferredID = "ProjectFilterTopComponent",
		//iconBase="SET/PATH/TO/ICON/HERE", 
		persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.tw.netbeans.project.switcher.ProjectFilterTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
		displayName = "#CTL_ProjectFilterAction",
		preferredID = "ProjectFilterTopComponent")
@Messages({
	"CTL_ProjectFilterAction=Project Switcher",
	"CTL_ProjectFilterTopComponent=Project Switcher Window",
	"HINT_ProjectFilterTopComponent=Visualizes a single project."
})
public final class ProjectFilterTopComponent extends TopComponent implements ExplorerManager.Provider {

	public ProjectFilterTopComponent() {
		initComponents();
		setName(Bundle.CTL_ProjectFilterTopComponent());
		setToolTipText(Bundle.HINT_ProjectFilterTopComponent());
		associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));

		projectChoicePanel.getExplorerManager().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
					final Node[] selectedNodes = projectChoicePanel.getExplorerManager().getSelectedNodes();
					if (selectedNodes != null && selectedNodes.length > 0) {
						manager.setRootContext(selectedNodes[0]);
					}
				}
			}
		});

		OpenProjects.getDefault().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				loadOpenProjectsList();
			}
		});

		WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
			@Override
			public void run() {
				loadOpenProjectsList();
			}
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        beanTreeView1 = new org.openide.explorer.view.BeanTreeView();
        projectChoicePanel = new org.tw.netbeans.project.switcher.ProjectChoicePanel();

        setLayout(new java.awt.BorderLayout());

        beanTreeView1.setBorder(null);
        add(beanTreeView1, java.awt.BorderLayout.CENTER);
        add(projectChoicePanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

	private void loadOpenProjectsList() {
		List<String> selectedNodesNames = new LinkedList<String>();
		Node[] selectedNodes = projectChoicePanel.getExplorerManager().getSelectedNodes();
		if (selectedNodes != null && selectedNodes.length > 0) {
			for (Node node : selectedNodes) {
				selectedNodesNames.add(node.getName());
			}
		}

		projectChoicePanel.getExplorerManager().setRootContext(new AbstractNode(new Children.Keys<Project>() {
			@Override
			protected void addNotify() {
				this.setKeys(OpenProjects.getDefault().getOpenProjects());
			}

			@Override
			protected Node[] createNodes(Project p) {
				LogicalViewProvider provider = p.getLookup().lookup(LogicalViewProvider.class);
				if (provider != null) {
					return new Node[]{provider.createLogicalView()};
				}
				return null;
			}
		}));

		Node rootContext = projectChoicePanel.getExplorerManager().getRootContext();
		List<Node> select = new LinkedList<Node>();
		for (Node node : rootContext.getChildren().getNodes(true)) {
			if (selectedNodesNames.contains(node.getName())) {
				select.add(node);
			}
		}
		try {
			projectChoicePanel.getExplorerManager().setSelectedNodes(select.toArray(new Node[0]));
		} catch (PropertyVetoException ex) {
			Exceptions.printStackTrace(ex);
		}

		if (projectChoicePanel.getExplorerManager().getSelectedNodes().length == 0) {
			Node n = projectChoicePanel.getExplorerManager().getRootContext().getChildren().getNodeAt(0);
			try {
				if (n == null) {
					projectChoicePanel.getExplorerManager().setSelectedNodes(new Node[0]);
				} else {
					projectChoicePanel.getExplorerManager().setSelectedNodes(new Node[]{n});
				}
			} catch (PropertyVetoException ex) {
				Exceptions.printStackTrace(ex);
			}
		}

	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.BeanTreeView beanTreeView1;
    private org.tw.netbeans.project.switcher.ProjectChoicePanel projectChoicePanel;
    // End of variables declaration//GEN-END:variables

	@Override
	public void componentOpened() {
	}

	@Override
	public void componentClosed() {
	}

	void writeProperties(java.util.Properties p) {
		// better to version settings since initial version as advocated at
		// http://wiki.apidesign.org/wiki/PropertyFiles
		p.setProperty("version", "1.0");
		// TODO store your settings
	}

	void readProperties(java.util.Properties p) {
		String version = p.getProperty("version");
		// TODO read your settings according to their version
	}
	ExplorerManager manager = new ExplorerManager();

	@Override
	public ExplorerManager getExplorerManager() {
		return manager;
	}
}
