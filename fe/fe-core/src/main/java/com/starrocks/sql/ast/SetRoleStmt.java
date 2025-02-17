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

package com.starrocks.sql.ast;

import com.starrocks.analysis.RedirectStatus;

import java.util.ArrayList;
import java.util.List;

// set role all -> roles = null, all = true
// set role all except role1, role2 -> roles = [role1, role2], all = true
// set role role1, role2 -> roles = [role1, role2], all = false;
public class SetRoleStmt extends StatementBase {
    private enum SetRoleType {
        ALL,
        DEFAULT,
        NONE,
        ROLE
    }

    private final List<String> roles = new ArrayList<>();
    private SetRoleType setRoleType;

    public SetRoleStmt(List<String> roles) {
        this.roles.addAll(roles);
        this.setRoleType = SetRoleType.ROLE;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setTypeAll() {
        setRoleType = SetRoleType.ALL;
    }

    public boolean isAll() {
        return setRoleType.equals(SetRoleType.ALL);
    }

    public void setTypeDefault() {
        setRoleType = SetRoleType.DEFAULT;
    }

    public boolean isDefault() {
        return setRoleType.equals(SetRoleType.DEFAULT);
    }

    public void setTypeNone() {
        setRoleType = SetRoleType.NONE;
    }

    public boolean isNone() {
        return setRoleType.equals(SetRoleType.NONE);
    }

    @Override
    public RedirectStatus getRedirectStatus() {
        return RedirectStatus.NO_FORWARD;
    }

    @Override
    public <R, C> R accept(AstVisitor<R, C> visitor, C context) {
        return visitor.visitSetRoleStatement(this, context);
    }
}