/* 
 Copyright (c) 2013 LDBC
 Linked Data Benchmark Council (http://www.ldbcouncil.org)
 
 This file is part of ldbc_snb_datagen.
 
 ldbc_snb_datagen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 ldbc_snb_datagen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with ldbc_snb_datagen.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 All Rights Reserved.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation;  only Version 2 of the License dated
 June 1991.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package ldbc.snb.datagen.entities.statictype.tag;

import ldbc.snb.datagen.generator.dictionary.Dictionaries;

public class FlashMobTag implements Comparable<FlashMobTag> {
    public int level;
    public long date;
    public double prob;
    public int tag;

    public int compareTo(FlashMobTag t) {
        if (this.date - t.date < 0) return -1;
        if (this.date - t.date > 0) return 1;
        if (this.date - t.date == 0) return 0;
        return 0;
    }

    public void copyTo(FlashMobTag t) {
        t.level = this.level;
        t.date = this.date;
        t.prob = this.prob;
        t.tag = this.tag;
    }

    public String toString() {
        return "Level: " + level + " Date: " + date + " Tag:" + Dictionaries.tags.getName(tag);
    }
}
