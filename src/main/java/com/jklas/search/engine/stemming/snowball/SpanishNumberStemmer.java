/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
// This file was generated automatically by the Snowball to Java compiler

package com.jklas.search.engine.stemming.snowball;

import com.jklas.search.engine.stemming.Stemmer;
import com.jklas.search.index.Term;


/**
 * This class was automatically generated by a Snowball to Java compiler 
 * It implements the stemming algorithm defined by a snowball script.
 */

public class SpanishNumberStemmer implements Stemmer {

	@Override
	public Term stem(Term original) {
		int stemPos = stem(original.getValue().toLowerCase().toCharArray(), original.getValue().length());

		return new Term( original.getValue().substring(0, stemPos) );
	}

	public int stem(char s[], int len) {
		  if (len < 5)
		      return len;
		    
		    for (int i = 0; i < len; i++)
		      switch(s[i]) {
		        case 'à': 
		        case 'á':
		        case 'â':
		        case 'ä': s[i] = 'a'; break;
		        case 'ò':
		        case 'ó':
		        case 'ô':
		        case 'ö': s[i] = 'o'; break;
		        case 'è':
		        case 'é':
		        case 'ê':
		        case 'ë': s[i] = 'e'; break;
		        case 'ù':
		        case 'ú':
		        case 'û':
		        case 'ü': s[i] = 'u'; break;
		        case 'ì':
		        case 'í':
		        case 'î':
		        case 'ï': s[i] = 'i'; break;
		      }
		    
		    switch(s[len-1]) {
		      case 'o':
		      case 'a':
		      case 'e': return len - 1;
		      case 's':
		        if (s[len-2] == 'e' && s[len-3] == 's' && s[len-4] == 'e')
		          return len-2;
		        if (s[len-2] == 'e' && s[len-3] == 'c') {
		          s[len-3] = 'z';
		          return len - 2;
		        }
		        if (s[len-2] == 'o' || s[len-2] == 'a' || s[len-2] == 'e')
		          return len - 2;
		    }
		    
		    return len;

	  }

	
}
