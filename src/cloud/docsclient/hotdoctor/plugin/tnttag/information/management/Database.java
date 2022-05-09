package cloud.docsclient.hotdoctor.plugin.tnttag.information.management;

import java.util.UUID;

import cloud.docsclient.hotdoctor.plugin.tnttag.utils.TNTPlayer;

public abstract class Database {

	public abstract TNTPlayer load(UUID player);

	public abstract void save(UUID player);


}
