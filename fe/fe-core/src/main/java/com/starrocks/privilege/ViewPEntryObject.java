// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.starrocks.privilege;

import com.starrocks.catalog.Database;
import com.starrocks.catalog.Table;
import com.starrocks.server.GlobalStateMgr;

import java.util.List;

/**
 * View is a subclass of table, only the table type is different
 */
public class ViewPEntryObject extends TablePEntryObject {
    protected ViewPEntryObject(long databaseId, long tableId) {
        super(databaseId, tableId);
    }

    public static ViewPEntryObject generate(GlobalStateMgr mgr, List<String> tokens) throws PrivilegeException {
        if (tokens.size() != 2) {
            throw new PrivilegeException("invalid object tokens, should have two: " + tokens);
        }
        long dbId;
        long tableId;

        if (tokens.get(0).equals("*")) {
            dbId = ALL_DATABASE_ID;
            tableId = ALL_TABLES_ID;
        } else {
            Database database = mgr.getDb(tokens.get(0));
            if (database == null) {
                throw new PrivObjNotFoundException("cannot find db: " + tokens.get(0));
            }
            dbId = database.getId();

            if (tokens.get(1).equals("*")) {
                tableId = ALL_TABLES_ID;
            } else {
                Table table = database.getTable(tokens.get(1));
                if (table == null || !table.getType().equals(Table.TableType.VIEW)) {
                    throw new PrivObjNotFoundException("cannot find view " + tokens.get(1) + " in db " + tokens.get(0));
                }
                tableId = table.getId();
            }
        }

        return new ViewPEntryObject(dbId, tableId);
    }
}
