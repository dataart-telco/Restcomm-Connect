/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.mobicents.servlet.restcomm.identity.exceptions;

/**
 * Thrown when an initial access token has expired or has already been used. In that case the administrator
 * should request a new one.
 *
 * @author Orestis Tsakiridis
 */
public class InitialAccessTokenExpired extends Exception {
    public InitialAccessTokenExpired() {
        super();
    }

    public InitialAccessTokenExpired(String message) {
        super(message);
    }
}