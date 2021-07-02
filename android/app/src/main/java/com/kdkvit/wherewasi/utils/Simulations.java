package com.kdkvit.wherewasi.utils;

import android.content.Context;

import models.Interaction;
import models.MyLocation;
import utils.DatabaseHandler;

public class Simulations {
    /**
     * Test - Simulate interactions data and add to db
     */
    public void simInteractionData(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);

        int i = 0;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 1 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 2 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 3 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 4 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 5 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 6 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 7 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 8 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 10 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 11 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 12 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 13 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 14 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 15 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 16 + (10 * i), true, false, true));

        i = 1;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 17 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 18 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 19 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 111 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 112 + (10 * i), true, true, true));

        i = 2;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 113 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 114 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 115 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 116 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 117 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 118 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 119 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 121 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 122 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 123 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 124 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 125 + (10 * i), true, false, true));

        i = 3;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 126 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 127 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 128 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 129 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 131 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 132 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 133 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 134 + (10 * i), true, false, true));

        i = 4;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 135 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 136 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 137 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 138 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 139 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 141 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 142 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 144 + (10 * i), true, false, true));

        i = 5;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 145 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 146 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 147 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 148 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 149 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 151 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 152 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 153 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 154 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 155 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 156 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 157 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 158 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 159 + (10 * i), true, false, true));

        i = 6;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 161 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 162 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 163 + (10 * i), true, false, true));
        db.close();

        i = 7;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 21 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 22 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 23 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 24 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 995 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 996 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 799 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 998 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 999 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9910 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 9911 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9912 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9913 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9914 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9915 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 9916 + (10 * i), true, false, true));

        i = 8;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 9917 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 9918 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 9919 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99111 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99112 + (10 * i), true, false, true));

        i = 9;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99113 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99114 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99115 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99116 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99117 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 99118 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99119 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99121 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99122 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99123 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99124 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99125 + (10 * i), true, true, true));

        i = 10;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99126 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99127 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99128 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99129 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99131 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99132 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99133 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99134 + (10 * i), true, false, true));

        i = 11;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99135 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99136 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99137 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99138 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99139 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99141 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99142 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99144 + (10 * i), true, false, true));

        i = 12;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99145 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99146 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99147 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99148 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99149 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624109760000") - (86400000 * i), Long.parseLong("1624109760000") - (86400000 * i), 0, 99151 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99152 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99153 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99154 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99155 + (10 * i), true, true, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624102560000") - (86400000 * i), Long.parseLong("1624102560000") - (86400000 * i), 0, 99156 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99157 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99158 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624084560000") - (86400000 * i), Long.parseLong("1624084560000") - (86400000 * i), 0, 99159 + (10 * i), true, false, true));

        i = 13;
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99161 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624127760000") - (86400000 * i), Long.parseLong("1624127760000") - (86400000 * i), 0, 99162 + (10 * i), true, false, true));
        db.addInteraction(new Interaction("abcd", Long.parseLong("1624120560000") - (86400000 * i), Long.parseLong("1624120560000") - (86400000 * i), 0, 99163 + (10 * i), true, false, true));
        db.close();
    }

    /**
     * Test - simulate locations data and add to db
     */
    public void simLocationData(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        int i = 0;
        db.addLocation(new MyLocation(1, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(2, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(3, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(4, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(5, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 1;
        db.addLocation(new MyLocation(6, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(7, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(8, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 2;
        db.addLocation(new MyLocation(9, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(11, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(12, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(13, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 3;
        db.addLocation(new MyLocation(14, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(15, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 4;
        db.addLocation(new MyLocation(16, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(17, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(18, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(19, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(121, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 5;
        db.addLocation(new MyLocation(122, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(123, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(124, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 6;
        db.addLocation(new MyLocation(125, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(126, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(127, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(128, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 7;
        db.addLocation(new MyLocation(91, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(92, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(93, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(94, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(95, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 8;
        db.addLocation(new MyLocation(96, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(97, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(98, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 9;
        db.addLocation(new MyLocation(99, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(911, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(912, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(913, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 10;
        db.addLocation(new MyLocation(914, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(915, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 11;
        db.addLocation(new MyLocation(916, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(917, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(918, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(919, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9121, 31.9977516, 34.764681, "fused", Long.parseLong("1624064400000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Long.parseLong("1624077000000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 12;
        db.addLocation(new MyLocation(9122, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9123, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9124, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

        i = 13;
        db.addLocation(new MyLocation(9125, 31.9977516, 34.764681, "fused", Long.parseLong("1624125600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Long.parseLong("1624128600000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9126, 31.9977516, 34.764681, "fused", Long.parseLong("1624096800000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Long.parseLong("1624111200000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9127, 31.9977516, 34.764681, "fused", Long.parseLong("1624091400000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Long.parseLong("1624096740000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));
        db.addLocation(new MyLocation(9128, 31.9977516, 34.764681, "fused", Long.parseLong("1624082400000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Long.parseLong("1624090260000") - (86400000 * i), Float.parseFloat("11.6759996414185"), "Center District", "IL", "27", "Uri Tsvi Grinberg St 27, Holon, Israel"));

    }
}
