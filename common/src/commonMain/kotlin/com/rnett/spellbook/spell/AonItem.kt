package com.rnett.spellbook.spell

interface AonLinkable {
    val aonUrl: String
}

interface AonItem : AonLinkable {
    val aonPage: String
    val aonId: Int

    override val aonUrl: String
        get() = "$aonPage.aspx?ID=$aonId"
}