/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.relational.commands;

import org.komodo.relational.RelationalObject;
import org.komodo.relational.model.OptionContainer;
import org.komodo.relational.model.internal.OptionContainerUtils;
import org.komodo.shell.CommandResultImpl;
import org.komodo.shell.api.CommandResult;
import org.komodo.shell.api.WorkspaceStatus;
import org.komodo.shell.commands.SetPropertyCommand;
import org.komodo.spi.KException;
import org.komodo.utils.i18n.I18n;

/**
 * A shell command to set a custom property on a {@link RelationalObject}.
 */
public final class SetCustomOptionCommand extends RelationalShellCommand {

    static final String NAME = "set-custom-option"; //$NON-NLS-1$

    /**
     * @param status
     *        the shell's workspace status (cannot be <code>null</code>)
     */
    public SetCustomOptionCommand( final WorkspaceStatus status ) {
        super( status, NAME );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#doExecute()
     */
    @Override
    protected CommandResult doExecute() {
        CommandResult result = null;
        //Cast is save since this command can be invoked only for objects implementing OptionContainer
        OptionContainer optionContainer=(OptionContainer)getContext();

        try {
            final String name = requiredArgument( 0, I18n.bind( RelationalCommandsI18n.missingOptionNameValue ) );
            final String value = requiredArgument( 1, I18n.bind( RelationalCommandsI18n.missingOptionNameValue ) );
            if(optionContainer.isStandardOption(name)){
            	 throw new KException(I18n.bind( RelationalCommandsI18n.useSetPropertyCommand, SetPropertyCommand.NAME ));
            }
            OptionContainerUtils.setOption(getTransaction(),optionContainer,name,value);
            result = new CommandResultImpl( I18n.bind( RelationalCommandsI18n.setCustomOptionSuccess, name ) );
        } catch ( final Exception e ) {
            result = new CommandResultImpl( e );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#getMaxArgCount()
     */
    @Override
    protected int getMaxArgCount() {
        return 2;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.api.ShellCommand#isValidForCurrentContext()
     */
    @Override
    public boolean isValidForCurrentContext() {
        return (getContext() instanceof OptionContainer);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpDescription(int)
     */
    @Override
    protected void printHelpDescription( final int indent ) {
        print( indent, I18n.bind( RelationalCommandsI18n.setCustomOptionHelp, getName() ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpExamples(int)
     */
    @Override
    protected void printHelpExamples( final int indent ) {
        print( indent, I18n.bind( RelationalCommandsI18n.setCustomOptionExamples ) );
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.shell.BuiltInShellCommand#printHelpUsage(int)
     */
    @Override
    protected void printHelpUsage( final int indent ) {
        print( indent, I18n.bind( RelationalCommandsI18n.setCustomOptionUsage ) );
    }


}