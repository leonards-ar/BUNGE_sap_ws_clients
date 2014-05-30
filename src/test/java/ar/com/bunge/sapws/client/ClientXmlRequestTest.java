package ar.com.bunge.sapws.client;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import ar.com.bunge.util.FileUtils;

/**
 * Unit test for simple App.
 */
public class ClientXmlRequestTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ClientXmlRequestTest( String testName )
    {
        super( testName );
    }

    public void testNestedLoops() throws Exception
    {
    	String requestXml = "init\n" +
    			"${xloopn(v)}\n" +
				"InitValue: ${#initVal#}\n" +
				"\t${loopn(j)}\n"
				+ "\t\tNestedVal: ${#nestedVal#}\n" +
				"\t{end loopn}\n"
				+ "EndValue: ${#endVal#}\n" +
				"{end xloopn}\n" +
				"end";

    	String expected = "init\n\nInitValue: initVal(1)\n\t\n\t\tNestedVal: nestedVal(1)(1)\n\t\nEndValue: endVal(1)\n\nInitValue: initVal(2)\n\t\n\t\tNestedVal: nestedVal(2)(1)\n\t\n\t\tNestedVal: nestedVal(2)(2)\n\t\nEndValue: endVal(2)\n\nend";

    	Map<String, Object> context = new HashMap<String, Object>();
    	context.put("v", "2");
    	context.put("j(1)", "1");
    	context.put("j(2)", "2");
    	context.put("initval(1)", "initVal(1)");
    	context.put("initval(2)", "initVal(2)");
    	context.put("endval(1)", "endVal(1)");
    	context.put("endval(2)", "endVal(2)");
    	context.put("nestedval(1)(1)", "nestedVal(1)(1)");
    	context.put("nestedval(2)(1)", "nestedVal(2)(1)");
    	context.put("nestedval(2)(2)", "nestedVal(2)(2)");
    	
    	ClientXmlRequest req = new ClientXmlRequest(requestXml);
    	req.compile(context);
    	
    	String result = req.getRequest();
    	
    	assertEquals(expected, result);

    }
    
    public void testNestedNestedLoops1() throws Exception
    {
    	String requestXml = "${xxloop(i)}\n" +
    	"${xloop(v)}\n" +
    	"\t${loop(j)}\n"
    	+ "\t\tShow me once\n" +
    	"\t{end loop}\n" +
    	"{end xloop}\n" +
    	"{end xxloop}";
 

    	String expected = "\n\n\t\n\t\tShow me once\n\t\n\n";

    	Map<String, Object> context = new HashMap<String, Object>();
    	context.put("i", "1");
    	context.put("v(1)", "1");
    	context.put("j(1)(1)", "1");
    	
    	ClientXmlRequest req = new ClientXmlRequest(requestXml);
    	req.compile(context);
    	
    	String result = req.getRequest();
    	
    	assertEquals(expected, result);

    }
    
    public void testNestedNestedLoops2() throws Exception
    {
    	String requestXml = "${xxloop(i)}\n" +
    	"${xloop(v)}\n" +
    	"\t${loop(j)}\n"
    	+ "\t\tShow me once\n" +
    	"\t{end loop}\n" +
    	"{end xloop}\n" +
    	"{end xxloop}";
 

    	String expected = "\n\n\t\n\t\tShow me once\n\t\n\n";

    	Map<String, Object> context = new HashMap<String, Object>();
    	
    	ClientXmlRequest req = new ClientXmlRequest(requestXml);
    	req.compile(context);
    	
    	String result = req.getRequest();
    	
    	assertEquals(expected, result);

    }
    
    public void testComplexNextedLoop() throws Exception {
    	String template = "   <urn:CupoNegocio xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Bunge.com:PI:Cupos\">\n" + 
    			"         <CupoCabecera>\n" + 
    			"            <EmpresaCUIT>${#EmpresaCUIT#}</EmpresaCUIT>\n" + 
    			"            <CupoID>${#CupoID#}</CupoID>\n" + 
    			"            <CupoOrigen>${#CupoOrigen#}</CupoOrigen>\n" + 
    			"            <FechaValidezDesde>${#FechaValidezDesde#}</FechaValidezDesde>\n" + 
    			"            <FechaValidezHasta>${#FechaValidezHasta#}</FechaValidezHasta>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <HoraValidezHasta>${#HoraValidezHasta#}</HoraValidezHasta>\n" + 
    			"            <CodigoOrgVta>${#CodigoOrgVta#}</CodigoOrgVta>\n" + 
    			"            <TipoOperacionID>${#TipoOperacionID#}</TipoOperacionID>\n" + 
    			"            <ActFechaUltima>${#ActFechaUltima#}</ActFechaUltima>\n" + 
    			"            <ActHoraUltima>${#ActHoraUltima#}</ActHoraUltima>\n" + 
    			"            <Virtual>${#Virtual#}</Virtual>\n" + 
    			"            <FleteACargo>${#FleteACargo#}</FleteACargo>\n" + 
    			"            <TranspTipoID>${#TranspTipoID#}</TranspTipoID>\n" + 
    			"            <TranspMedioID>${#TranspMedioID#}</TranspMedioID>\n" + 
    			"            <PesoOrigenDestino>${#PesoOrigenDestino#}</PesoOrigenDestino>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroOrdenCargaDesc>${#NroOrdenCargaDesc#}</NroOrdenCargaDesc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NumeroST>${#NumeroST#}</NumeroST>\n" + 
    			"            <CupoNegocioEst>${#CupoNegocioEst#}</CupoNegocioEst>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EstadoDsc>${#EstadoDsc#}</EstadoDsc>\n" + 
    			"            <CupoFechaCreacion>${#CupoFechaCreacion#}</CupoFechaCreacion>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <CupoHoraCreacion>${#CupoHoraCreacion#}</CupoHoraCreacion>\n" + 
    			"            <UsuarioCreacion>${#UsuarioCreacion#}</UsuarioCreacion>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <OrigenPlantaID>${#OrigenPlantaID#}</OrigenPlantaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <OrigenAlmacenID>${#OrigenAlmacenID#}</OrigenAlmacenID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <DestinoPlantaID>${#DestinoPlantaID#}</DestinoPlantaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <DestinoAlmacenID>${#DestinoAlmacenID#}</DestinoAlmacenID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ClienteProveedor>${#ClienteProveedor#}</ClienteProveedor>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadID>${#EntidadID#}</EntidadID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadCUIT>${#EntidadCUIT#}</EntidadCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <GrupoCUIT>${#GrupoCUIT#}</GrupoCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <IdentificadorExt>${#IdentificadorExt#}</IdentificadorExt>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadRazsoc>${#EntidadRazsoc#}</EntidadRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadDir>${#EntidadDir#}</EntidadDir>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadLocalidadCP>${#EntidadLocalidadCP#}</EntidadLocalidadCP>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadLocalidadDsc>${#EntidadLocalidadDsc#}</EntidadLocalidadDsc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadProvinciaID>${#EntidadProvinciaID#}</EntidadProvinciaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadPaisID>${#EntidadPaisID#}</EntidadPaisID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadCndIvaID>${#EntidadCndIvaID#}</EntidadCndIvaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadIIBB>${#EntidadIIBB#}</EntidadIIBB>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PatenteChasis>${#PatenteChasis#}</PatenteChasis>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PatenteAcoplado>${#PatenteAcoplado#}</PatenteAcoplado>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroOperativo>${#NroOperativo#}</NroOperativo>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroVagon>${#NroVagon#}</NroVagon>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspCUIT>${#TranspCUIT#}</TranspCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspRazsoc>${#TranspRazsoc#}</TranspRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspFactCUIT>${#TranspFactCUIT#}</TranspFactCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspFactRazsoc>${#TranspFactRazsoc#}</TranspFactRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ChoferCUILCUIT>${#ChoferCUILCUIT#}</ChoferCUILCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ChoferApellidoNom>${#ChoferApellidoNom#}</ChoferApellidoNom>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <CampoID>${#CampoID#}</CampoID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PuestoID>${#PuestoID#}</PuestoID>\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			${loop(CupoInterlocutor)}\n" + 
    			"            <CupoInterlocutor>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <InterlocutorCodFunc>${#InterlocutorCodFunc#}</InterlocutorCodFunc>\n" + 
    			"               <InterlocutorCod>${#InterlocutorCod#}</InterlocutorCod>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodPostal>${#CodPostal#}</CodPostal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <SubCodPostal>${#SubCodPostal#}</SubCodPostal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <LocalidadDsc>${#LocalidadDsc#}</LocalidadDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Direccion>${#Direccion#}</Direccion>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProvinciaID>${#ProvinciaID#}</ProvinciaID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PaisID>${#PaisID#}</PaisID>\n" + 
    			"            </CupoInterlocutor>\n" + 
    			"			{end loop}\n" + 
    			"            <!--1 or more repetitions:-->\n" + 
    			"			${xxloop(CupoDetalles)}\n" + 
    			"            <CupoDetalles>\n" + 
    			"               <PosicionCupo>${#PosicionCupo#}</PosicionCupo>\n" + 
    			"               <ProductoID>${#CupoDetallesProductoID#}</ProductoID>\n" + 
    			"               <ClaseVal>${#CupoDetallesClaseVal#}</ClaseVal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <NroCuentaVtaComp>${#NroCuentaVtaComp#}</NroCuentaVtaComp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrNroRenglon>${#ContrNroRenglon#}</ContrNroRenglon>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrFechaCreacion>${#ContrFechaCreacion#}</ContrFechaCreacion>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrCantTotal>${#ContrCantTotal#}</ContrCantTotal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrCantTotalKG>${#ContrCantTotalKG#}</ContrCantTotalKG>\n" + 
    			"               <ProductoUM>${#CupoDetallesProductoUM#}</ProductoUM>\n" + 
    			"               <CantidadDesp>${#CantidadDesp#}</CantidadDesp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <KilosDesp>${#KilosDesp#}</KilosDesp>\n" + 
    			"               <PorcTolMas>${#PorcTolMas#}</PorcTolMas>\n" + 
    			"               <PorcTolMenos>${#PorcTolMenos#}</PorcTolMenos>\n" + 
    			"               <ClaseDoc>${#ClaseDoc#}</ClaseDoc>\n" + 
    			"               <ViaPago>${#ViaPago#}</ViaPago>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <NroOrdenComp>${#NroOrdenComp#}</NroOrdenComp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoPrecio>${#ProductoPrecio#}</ProductoPrecio>\n" + 
    			"               <!--Zero or more repetitions:-->\n" + 
    			"               ${xloop(DetCompProductos)}\n" + 
    			"               <DetCompProductos>\n" + 
    			"                  <VersionFab>${#VersionFab#}</VersionFab>\n" + 
    			"                  <PosicionForm>${#PosicionForm#}</PosicionForm>\n" + 
    			"                  <CodMaterialComp>${#CodMaterialComp#}</CodMaterialComp>\n" + 
    			"                  <ClaseVal>${#DetCompProductosClaseVal#}</ClaseVal>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <Cantidad>${#Cantidad#}</Cantidad>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <UM>${#UM#}</UM>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <CantidadKG>${#CantidadKG#}</CantidadKG>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <PorcParticipacion>${#PorcParticipacion#}</PorcParticipacion>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <AdmSustitutos>${#AdmSustitutos#}</AdmSustitutos>\n" + 
    			"                  <!--Zero or more repetitions:-->\n" + 
    			"                  ${loop(DetCompSustitutos)}\n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>${#CodMaterialSust#}</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>${#Prioridad#}</Prioridad>\n" + 
    			"                     <ClaseVal>${#DetCompSustitutosClaseVal#}</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  {end loop}\n" + 
    			"               </DetCompProductos>\n" + 
    			"               {end xloop}\n" + 
    			"            </CupoDetalles>\n" + 
    			"			{end xxloop}\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"            ${loop(CupoBloqueo)}\n" + 
    			"			<CupoBloqueo>\n" + 
    			"               <BloqueoTipo>${#BloqueoTipo#}</BloqueoTipo>\n" + 
    			"               <!--1 or more repetitions:-->\n" + 
    			"               <BloqueoDsc>${#BloqueoDsc#}</BloqueoDsc>\n" + 
    			"            </CupoBloqueo>\n" + 
    			"			{end loop}\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			${loop(CupoProductos)}\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>${#ProductoID#}</ProductoID>\n" + 
    			"               <ClaseVal>${#CupoProductosClaseVal#}</ClaseVal>\n" + 
    			"               <ProductoDsc>${#ProductoDsc#}</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>${#ProductoCodTipo#}</ProductoCodTipo>\n" + 
    			"               <ProductoUM>${#ProductoUM#}</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>${#ProductoEstado#}</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>${#PuroMezcla#}</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>${#MaterialPeligroso#}</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable>${#Stokeable#}</Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>${#Nitrogeno#}</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>${#Fosforo#}</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>${#Potasio#}</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre>${#Azufre#}</Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio>${#Calcio#}</Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>${#Magnesio#}</Magnesio>\n" + 
    			"               <ReqPrecinto>${#ReqPrecinto#}</ReqPrecinto>\n" + 
    			"               <ReqTapas>${#ReqTapas#}</ReqTapas>\n" + 
    			"               <ReqDensidad>${#ReqDensidad#}</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>${#ImpCartillaSeg#}</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>${#CheckList#}</CheckList>\n" + 
    			"               <ControlSat>${#ControlSat#}</ControlSat>\n" + 
    			"               <RelRUCCA>${#RelRUCCA#}</RelRUCCA>\n" + 
    			"               <CodUnicoNom>${#CodUnicoNom#}</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>${#CodLeyenda#}</CodLeyenda>\n" + 
    			"               <MermaCalidad>${#MermaCalidad#}</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>${#ConsCapPlanta#}</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA>${#CodProdARBA#}</CodProdARBA>\n" + 
    			"               <EsEnvase>${#EsEnvase#}</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>${#ProductoPresentacionID#}</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			{end loop}\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			${loop(CupoPresentaciones)}\n" + 
    			"            <CupoPresentaciones>\n" + 
    			"               <PresentacionID>${#PresentacionID#}</PresentacionID>\n" + 
    			"               <PresentacionDsc>${#PresentacionDsc#}</PresentacionDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoID>${#PresentacionBultoID#}</BultoID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nivel>${#Nivel#}</Nivel>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoCantidad>${#BultoCantidad#}</BultoCantidad>\n" + 
    			"            </CupoPresentaciones>\n" + 
    			"			{end loop}\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			${loop(CupoBultos)}\n" + 
    			"            <CupoBultos>\n" + 
    			"               <BultoID>${#BultoID#}</BultoID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoDsc>${#BultoDsc#}</BultoDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvaseCapacidad>${#EnvaseCapacidad#}</EnvaseCapacidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvaseUM>${#EnvaseUM#}</EnvaseUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvasePeso>${#EnvasePeso#}</EnvasePeso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvasePesoUM>${#EnvasePesoUM#}</EnvasePesoUM>\n" + 
    			"            </CupoBultos>\n" + 
    			"			{end loop}\n" + 
    			"         </CupoCabecera>\n" + 
    			"      </urn:CupoNegocio>\n" + 
    			"";
    	Map<String, Object> context = FileUtils.parseKeyValueString("EmpresaCUIT=30700869918                                                         \n" + 
    			"CupoID=0000361795                                                               \n" + 
    			"CupoOrigen=C                                                                    \n" + 
    			"FechaValidezDesde=2014/04/24                                                    \n" + 
    			"FechaValidezHasta=2014/04/25                                                    \n" + 
    			"HoraValidezHasta=080000                                                         \n" + 
    			"CodigoOrgVta=FERT                                                               \n" + 
    			"TipoOperacionID=D                                                               \n" + 
    			"ActFechaUltima=2014/04/24                                                       \n" + 
    			"ActHoraUltima=102001                                                            \n" + 
    			"Virtual=N                                                                       \n" + 
    			"FleteACargo=                                                                    \n" + 
    			"TranspTipoID=10                                                                 \n" + 
    			"TranspMedioID=04                                                                \n" + 
    			"PesoOrigenDestino=ORIG                                                          \n" + 
    			"NroOrdenCargaDesc=0000000000                                                    \n" + 
    			"NumeroST=                                                                       \n" + 
    			"CupoNegocioEst=S                                                                \n" + 
    			"EstadoDsc=                                                                      \n" + 
    			"CupoFechaCreacion=2014/04/24                                                    \n" + 
    			"CupoHoraCreacion=102001                                                         \n" + 
    			"UsuarioCreacion=extacuevas                                                      \n" + 
    			"OrigenPlantaID=1400                                                             \n" + 
    			"OrigenAlmacenID=FD01                                                            \n" + 
    			"DestinoPlantaID=                                                                \n" + 
    			"DestinoAlmacenID=                                                               \n" + 
    			"ClienteProveedor=C                                                              \n" + 
    			"EntidadID=0000313363                                                            \n" + 
    			"EntidadCUIT=30685146335                                                         \n" + 
    			"GrupoCUIT=80                                                                    \n" + 
    			"IdentificadorExt=                                                               \n" + 
    			"EntidadRazsoc=DESAB S.A.                                                        \n" + 
    			"EntidadDir=RUTA 33-KM 532                                                       \n" + 
    			"EntidadLocalidadCP=6100                                                         \n" + 
    			"EntidadLocalidadDsc=RUFINO                                                      \n" + 
    			"EntidadProvinciaID=12                                                           \n" + 
    			"EntidadPaisID=AR                                                                \n" + 
    			"EntidadCndIvaID=01                                                              \n" + 
    			"EntidadIIBB=9021743865                                                          \n" + 
    			"PatenteChasis=                                                                  \n" + 
    			"PatenteAcoplado=                                                                \n" + 
    			"NroOperativo=                                                                   \n" + 
    			"NroVagon=0000000000                                                             \n" + 
    			"TranspCUIT=                                                                     \n" + 
    			"TranspRazsoc=                                                                   \n" + 
    			"TranspFactCUIT=                                                                 \n" + 
    			"TranspFactRazsoc=                                                               \n" + 
    			"ChoferCUILCUIT=                                                                 \n" + 
    			"ChoferApellidoNom=                                                              \n" + 
    			"CampoID=01                                                                      \n" + 
    			"PuestoID=01                                                                     \n" + 
    			"CupoInterlocutor=01                                                             \n" + 
    			"InterlocutorCodFunc(01)=                                                        \n" + 
    			"InterlocutorCod(01)=                                                            \n" + 
    			"CodPostal(01)=6100                                                              \n" + 
    			"SubCodPostal(01)=0                                                              \n" + 
    			"LocalidadDsc(01)=RUFINO                                                         \n" + 
    			"Direccion(01)=RUFINO                                                            \n" + 
    			"ProvinciaID(01)=12                                                              \n" + 
    			"PaisID(01)=AR                                                                   \n" + 
    			"CupoDetalles=01                                                                 \n" + 
    			"PosicionCupo(01)=01                                                             \n" + 
    			"CupoDetallesProductoID(01)=FLM9SX28GR0000                                                   \n" + 
    			"CupoDetallesClaseVal(01)=FABRI                                                              \n" + 
    			"NroCuentaVtaComp(01)=140400000238                                               \n" + 
    			"ContrNroRenglon(01)=000001                                                      \n" + 
    			"ContrFechaCreacion(01)=2014/04/24                                               \n" + 
    			"ContrCantTotal(01)=1600,000                                                     \n" + 
    			"ContrCantTotalKG(01)=1600000,000                                                \n" + 
    			"CupoDetallesProductoUM(01)=TO                                                               \n" + 
    			"CantidadDesp(01)=35,000                                                         \n" + 
    			"KilosDesp(01)=35000,000                                                         \n" + 
    			"PorcTolMas(01)=                                                                 \n" + 
    			"PorcTolMenos(01)=                                                               \n" + 
    			"ClaseDoc(01)=V                                                                  \n" + 
    			"ViaPago(01)=                                                                    \n" + 
    			"NroOrdenComp(01)=                                                               \n" + 
    			"ProductoPrecio(01)=2135,17                                                      \n" + 
    			"DetCompProductos(01)=04                                                             \n" + 
    			"VersionFab(01)(01)=0150                                                         \n" + 
    			"PosicionForm(01)(01)=                                                           \n" + 
    			"CodMaterialComp(01)(01)=FLP9TSA0GR0000                                          \n" + 
    			"DetCompProductosClaseVal(01)(01)=LOCAL                                                          \n" + 
    			"Cantidad(01)(01)=0000000000007000                                               \n" + 
    			"UM(01)(01)=TO                                                                   \n" + 
    			"CantidadKG(01)(01)=000000000007000000                                           \n" + 
    			"PorcParticipacion(01)(01)=02000                                                 \n" + 
    			"AdmSustitutos(01)(01)=S                                                         \n" + 
    			"DetCompSustitutos(01)(01)=02                                                            \n" + 
    			"CodMaterialSust(01)(01)(01)=FLP9TSA0GR0000                                      \n" + 
    			"Prioridad(01)(01)(01)=01                                                        \n" + 
    			"DetCompSustitutosClaseVal(01)(01)(01)=IMPOR                                                      \n" + 
    			"CodMaterialSust(01)(01)(02)=FLP9TSA0GR0000                                      \n" + 
    			"Prioridad(01)(01)(02)=02                                                        \n" + 
    			"DetCompSustitutosClaseVal(01)(01)(02)=FABRI                                                      \n" + 
    			"VersionFab(01)(02)=0150                                                         \n" + 
    			"PosicionForm(01)(02)=                                                           \n" + 
    			"CodMaterialComp(01)(02)=FLP9UAN0GR0000                                          \n" + 
    			"DetCompProductosClaseVal(01)(02)=FABRI                                                          \n" + 
    			"Cantidad(01)(02)=0000000000028000                                               \n" + 
    			"UM(01)(02)=TO                                                                   \n" + 
    			"CantidadKG(01)(02)=000000000028000000                                           \n" + 
    			"PorcParticipacion(01)(02)=08000                                                 \n" + 
    			"AdmSustitutos(01)(02)=S                                                         \n" + 
    			"DetCompSustitutos(01)(02)=01                                                            \n" + 
    			"CodMaterialSust(01)(02)(01)=FLP9UAN0GR0000                                      \n" + 
    			"Prioridad(01)(02)(01)=01                                                        \n" + 
    			"DetCompSustitutosClaseVal(01)(02)(01)=IMPOR                                                      \n" + 
    			"VersionFab(01)(03)=0150                                                         \n" + 
    			"PosicionForm(01)(03)=                                                           \n" + 
    			"CodMaterialComp(01)(03)=FLP9TSA0GR0000                                          \n" + 
    			"DetCompProductosClaseVal(01)(03)=LOCAL                                                          \n" + 
    			"Cantidad(01)(03)=0000000000003885                                               \n" + 
    			"UM(01)(03)=TO                                                                   \n" + 
    			"CantidadKG(01)(03)=000000000003885000                                           \n" + 
    			"PorcParticipacion(01)(03)=01110                                                 \n" + 
    			"AdmSustitutos(01)(03)=S                                                         \n" + 
    			"DetCompSustitutos(01)(03)=02                                                            \n" + 
    			"CodMaterialSust(01)(03)(01)=FLP9TSA0GR0000                                      \n" + 
    			"Prioridad(01)(03)(01)=01                                                        \n" + 
    			"DetCompSustitutosClaseVal(01)(03)(01)=IMPOR                                                      \n" + 
    			"CodMaterialSust(01)(03)(02)=FLP9TSA0GR0000                                      \n" + 
    			"Prioridad(01)(03)(02)=02                                                        \n" + 
    			"DetCompSustitutosClaseVal(01)(03)(02)=FABRI                                                      \n" + 
    			"VersionFab(01)(04)=0150                                                         \n" + 
    			"PosicionForm(01)(04)=                                                           \n" + 
    			"CodMaterialComp(01)(04)=FLM9SX30GR0000                                          \n" + 
    			"DetCompProductosClaseVal(01)(04)=FABRI                                                          \n" + 
    			"Cantidad(01)(04)=0000000000031115                                               \n" + 
    			"UM(01)(04)=TO                                                                   \n" + 
    			"CantidadKG(01)(04)=000000000031115000                                           \n" + 
    			"PorcParticipacion(01)(04)=08890                                                 \n" + 
    			"AdmSustitutos(01)(04)=S                                                         \n" + 
    			"CupoProductos=07                                                                \n" + 
    			"ProductoID(01)=FLP9UAN0GR0000                                                   \n" + 
    			"CupoProductosClaseVal(01)=IMPOR                                                              \n" + 
    			"ProductoDsc(01)=SOLUAN                                                          \n" + 
    			"ProductoCodTipo(01)=Fer                                                         \n" + 
    			"ProductoUM(01)=TO                                                               \n" + 
    			"ProductoEstado(01)=Liq                                                          \n" + 
    			"PuroMezcla(01)=Pur                                                              \n" + 
    			"MaterialPeligroso(01)=N                                                         \n" + 
    			"Stokeable(01)=                                                                  \n" + 
    			"Nitrogeno(01)=32,000                                                            \n" + 
    			"Fosforo(01)=0,000                                                               \n" + 
    			"Potasio(01)=0,000                                                               \n" + 
    			"Azufreo(01)=0,000                                                               \n" + 
    			"Calcioo(01)=0,000                                                               \n" + 
    			"Magnesio(01)=0,000                                                              \n" + 
    			"ReqPrecinto(01)=SI                                                              \n" + 
    			"ReqTapas(01)=Si                                                                 \n" + 
    			"ReqDensidad(01)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(01)=Si                                                           \n" + 
    			"CheckList(01)=No                                                                \n" + 
    			"ControlSat(01)=No                                                               \n" + 
    			"RelRUCCA(01)=No                                                                 \n" + 
    			"CodUnicoNom(01)=310280                                                          \n" + 
    			"CodLeyenda(01)=3                                                                \n" + 
    			"MermaCalidad(01)=No                                                             \n" + 
    			"ConsCapPlanta(01)=Si                                                            \n" + 
    			"CodProdARBA(01)=                                                                \n" + 
    			"EsEnvase(01)=N                                                                  \n" + 
    			"ProductoPresentacionID(01)=GR000                                                        \n" + 
    			"ProductoID(02)=FLP9TSA0GR0000                                                   \n" + 
    			"CupoProductosClaseVal(02)=LOCAL                                                              \n" + 
    			"ProductoDsc(02)=SOLPLUS 12-0-0-26S                                              \n" + 
    			"ProductoCodTipo(02)=Fer                                                         \n" + 
    			"ProductoUM(02)=TO                                                               \n" + 
    			"ProductoEstado(02)=Liq                                                          \n" + 
    			"PuroMezcla(02)=Pur                                                              \n" + 
    			"MaterialPeligroso(02)=N                                                         \n" + 
    			"Stokeable(02)=                                                                  \n" + 
    			"Nitrogeno(02)=12,000                                                            \n" + 
    			"Fosforo(02)=0,000                                                               \n" + 
    			"Potasio(02)=0,000                                                               \n" + 
    			"Azufreo(02)=26,000                                                              \n" + 
    			"Calcioo(02)=0,000                                                               \n" + 
    			"Magnesio(02)=0,000                                                              \n" + 
    			"ReqPrecinto(02)=SI                                                              \n" + 
    			"ReqTapas(02)=Si                                                                 \n" + 
    			"ReqDensidad(02)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(02)=Si                                                           \n" + 
    			"CheckList(02)=No                                                                \n" + 
    			"ControlSat(02)=No                                                               \n" + 
    			"RelRUCCA(02)=No                                                                 \n" + 
    			"CodUnicoNom(02)=310520                                                          \n" + 
    			"CodLeyenda(02)=3                                                                \n" + 
    			"MermaCalidad(02)=No                                                             \n" + 
    			"ConsCapPlanta(02)=Si                                                            \n" + 
    			"CodProdARBA(02)=                                                                \n" + 
    			"EsEnvase(02)=N                                                                  \n" + 
    			"ProductoPresentacionID(02)=GR000                                                        \n" + 
    			"ProductoID(03)=FLP9UAN0GR0000                                                   \n" + 
    			"CupoProductosClaseVal(03)=FABRI                                                              \n" + 
    			"ProductoDsc(03)=SOLUAN                                                          \n" + 
    			"ProductoCodTipo(03)=Fer                                                         \n" + 
    			"ProductoUM(03)=TO                                                               \n" + 
    			"ProductoEstado(03)=Liq                                                          \n" + 
    			"PuroMezcla(03)=Pur                                                              \n" + 
    			"MaterialPeligroso(03)=N                                                         \n" + 
    			"Stokeable(03)=                                                                  \n" + 
    			"Nitrogeno(03)=32,000                                                            \n" + 
    			"Fosforo(03)=0,000                                                               \n" + 
    			"Potasio(03)=0,000                                                               \n" + 
    			"Azufreo(03)=0,000                                                               \n" + 
    			"Calcioo(03)=0,000                                                               \n" + 
    			"Magnesio(03)=0,000                                                              \n" + 
    			"ReqPrecinto(03)=SI                                                              \n" + 
    			"ReqTapas(03)=Si                                                                 \n" + 
    			"ReqDensidad(03)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(03)=Si                                                           \n" + 
    			"CheckList(03)=No                                                                \n" + 
    			"ControlSat(03)=No                                                               \n" + 
    			"RelRUCCA(03)=No                                                                 \n" + 
    			"CodUnicoNom(03)=310280                                                          \n" + 
    			"CodLeyenda(03)=3                                                                \n" + 
    			"MermaCalidad(03)=No                                                             \n" + 
    			"ConsCapPlanta(03)=Si                                                            \n" + 
    			"CodProdARBA(03)=                                                                \n" + 
    			"EsEnvase(03)=N                                                                  \n" + 
    			"ProductoPresentacionID(03)=GR000                                                        \n" + 
    			"ProductoID(04)=FLM9SX30GR0000                                                   \n" + 
    			"CupoProductosClaseVal(04)=FABRI                                                              \n" + 
    			"ProductoDsc(04)=SOLMIX 30-0-0-2,6 SOLMIX                                        \n" + 
    			"ProductoCodTipo(04)=Fer                                                         \n" + 
    			"ProductoUM(04)=TO                                                               \n" + 
    			"ProductoEstado(04)=Liq                                                          \n" + 
    			"PuroMezcla(04)=Mez                                                              \n" + 
    			"MaterialPeligroso(04)=N                                                         \n" + 
    			"Stokeable(04)=                                                                  \n" + 
    			"Nitrogeno(04)=30,000                                                            \n" + 
    			"Fosforo(04)=0,000                                                               \n" + 
    			"Potasio(04)=0,000                                                               \n" + 
    			"Azufreo(04)=2,600                                                               \n" + 
    			"Calcioo(04)=0,000                                                               \n" + 
    			"Magnesio(04)=0,000                                                              \n" + 
    			"ReqPrecinto(04)=SI                                                              \n" + 
    			"ReqTapas(04)=Si                                                                 \n" + 
    			"ReqDensidad(04)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(04)=Si                                                           \n" + 
    			"CheckList(04)=No                                                                \n" + 
    			"ControlSat(04)=No                                                               \n" + 
    			"RelRUCCA(04)=No                                                                 \n" + 
    			"CodUnicoNom(04)=310520                                                          \n" + 
    			"CodLeyenda(04)=3                                                                \n" + 
    			"MermaCalidad(04)=No                                                             \n" + 
    			"ConsCapPlanta(04)=Si                                                            \n" + 
    			"CodProdARBA(04)=                                                                \n" + 
    			"EsEnvase(04)=N                                                                  \n" + 
    			"ProductoPresentacionID(04)=GR000                                                        \n" + 
    			"ProductoID(05)=FLM9SX28GR0000                                                   \n" + 
    			"CupoProductosClaseVal(05)=FABRI                                                              \n" + 
    			"ProductoDsc(05)=SOLMIX 28-0-0-5,2S    BUNGE                                     \n" + 
    			"ProductoCodTipo(05)=Fer                                                         \n" + 
    			"ProductoUM(05)=TO                                                               \n" + 
    			"ProductoEstado(05)=Liq                                                          \n" + 
    			"PuroMezcla(05)=Mez                                                              \n" + 
    			"MaterialPeligroso(05)=N                                                         \n" + 
    			"Stokeable(05)=                                                                  \n" + 
    			"Nitrogeno(05)=28,000                                                            \n" + 
    			"Fosforo(05)=0,000                                                               \n" + 
    			"Potasio(05)=0,000                                                               \n" + 
    			"Azufreo(05)=5,200                                                               \n" + 
    			"Calcioo(05)=0,000                                                               \n" + 
    			"Magnesio(05)=0,000                                                              \n" + 
    			"ReqPrecinto(05)=SI                                                              \n" + 
    			"ReqTapas(05)=Si                                                                 \n" + 
    			"ReqDensidad(05)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(05)=Si                                                           \n" + 
    			"CheckList(05)=No                                                                \n" + 
    			"ControlSat(05)=No                                                               \n" + 
    			"RelRUCCA(05)=No                                                                 \n" + 
    			"CodUnicoNom(05)=310520                                                          \n" + 
    			"CodLeyenda(05)=3                                                                \n" + 
    			"MermaCalidad(05)=No                                                             \n" + 
    			"ConsCapPlanta(05)=Si                                                            \n" + 
    			"CodProdARBA(05)=                                                                \n" + 
    			"EsEnvase(05)=N                                                                  \n" + 
    			"ProductoPresentacionID(05)=GR000                                                        \n" + 
    			"ProductoID(06)=FLP9TSA0GR0000                                                   \n" + 
    			"CupoProductosClaseVal(06)=IMPOR                                                              \n" + 
    			"ProductoDsc(06)=SOLPLUS 12-0-0-26S                                              \n" + 
    			"ProductoCodTipo(06)=Fer                                                         \n" + 
    			"ProductoUM(06)=TO                                                               \n" + 
    			"ProductoEstado(06)=Liq                                                          \n" + 
    			"PuroMezcla(06)=Pur                                                              \n" + 
    			"MaterialPeligroso(06)=N                                                         \n" + 
    			"Stokeable(06)=                                                                  \n" + 
    			"Nitrogeno(06)=12,000                                                            \n" + 
    			"Fosforo(06)=0,000                                                               \n" + 
    			"Potasio(06)=0,000                                                               \n" + 
    			"Azufreo(06)=26,000                                                              \n" + 
    			"Calcioo(06)=0,000                                                               \n" + 
    			"Magnesio(06)=0,000                                                              \n" + 
    			"ReqPrecinto(06)=SI                                                              \n" + 
    			"ReqTapas(06)=Si                                                                 \n" + 
    			"ReqDensidad(06)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(06)=Si                                                           \n" + 
    			"CheckList(06)=No                                                                \n" + 
    			"ControlSat(06)=No                                                               \n" + 
    			"RelRUCCA(06)=No                                                                 \n" + 
    			"CodUnicoNom(06)=310520                                                          \n" + 
    			"CodLeyenda(06)=3                                                                \n" + 
    			"MermaCalidad(06)=No                                                             \n" + 
    			"ConsCapPlanta(06)=Si                                                            \n" + 
    			"CodProdARBA(06)=                                                                \n" + 
    			"EsEnvase(06)=N                                                                  \n" + 
    			"ProductoPresentacionID(06)=GR000                                                        \n" + 
    			"ProductoID(07)=FLP9TSA0GR0000                                                   \n" + 
    			"CupoProductosClaseVal(07)=FABRI                                                              \n" + 
    			"ProductoDsc(07)=SOLPLUS 12-0-0-26S                                              \n" + 
    			"ProductoCodTipo(07)=Fer                                                         \n" + 
    			"ProductoUM(07)=TO                                                               \n" + 
    			"ProductoEstado(07)=Liq                                                          \n" + 
    			"PuroMezcla(07)=Pur                                                              \n" + 
    			"MaterialPeligroso(07)=N                                                         \n" + 
    			"Stokeable(07)=                                                                  \n" + 
    			"Nitrogeno(07)=12,000                                                            \n" + 
    			"Fosforo(07)=0,000                                                               \n" + 
    			"Potasio(07)=0,000                                                               \n" + 
    			"Azufreo(07)=26,000                                                              \n" + 
    			"Calcioo(07)=0,000                                                               \n" + 
    			"Magnesio(07)=0,000                                                              \n" + 
    			"ReqPrecinto(07)=SI                                                              \n" + 
    			"ReqTapas(07)=Si                                                                 \n" + 
    			"ReqDensidad(07)=Despacho                                                        \n" + 
    			"ImpCartillaSeg(07)=Si                                                           \n" + 
    			"CheckList(07)=No                                                                \n" + 
    			"ControlSat(07)=No                                                               \n" + 
    			"RelRUCCA(07)=No                                                                 \n" + 
    			"CodUnicoNom(07)=310520                                                          \n" + 
    			"CodLeyenda(07)=3                                                                \n" + 
    			"MermaCalidad(07)=No                                                             \n" + 
    			"ConsCapPlanta(07)=Si                                                            \n" + 
    			"CodProdARBA(07)=                                                                \n" + 
    			"EsEnvase(07)=N                                                                  \n" + 
    			"ProductoPresentacionID(07)=GR000                                                        \n" + 
    			"CupoPresentaciones=01                                                           \n" + 
    			"PresentacionID(01)=GR000                                                        \n" + 
    			"PresentacionDsc(01)=GRANEL                                                      \n" + 
    			"PresentacionBultoID(01)=GRL0                                                                \n" + 
    			"Nivel(01)=00                                                                    \n" + 
    			"BultoCantidad(01)=0                                                             \n" + 
    			"CupoBultos=01                                                                   \n" + 
    			"BultoID(01)=GRL0                                                                \n" + 
    			"BultoDsc(01)=GRANEL                                                             \n" + 
    			"EnvaseCapacidad(01)=0,000                                                       \n" + 
    			"EnvaseUM(01)=                                                                   \n" + 
    			"EnvasePeso(01)=0,000                                                            \n" + 
    			"EnvasePesoUM(01)=                                                               \n" + 
    			"");
    	String expected = "   <urn:CupoNegocio xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:Bunge.com:PI:Cupos\">\n" + 
    			"         <CupoCabecera>\n" + 
    			"            <EmpresaCUIT>30700869918</EmpresaCUIT>\n" + 
    			"            <CupoID>0000361795</CupoID>\n" + 
    			"            <CupoOrigen>C</CupoOrigen>\n" + 
    			"            <FechaValidezDesde>2014/04/24</FechaValidezDesde>\n" + 
    			"            <FechaValidezHasta>2014/04/25</FechaValidezHasta>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <HoraValidezHasta>080000</HoraValidezHasta>\n" + 
    			"            <CodigoOrgVta>FERT</CodigoOrgVta>\n" + 
    			"            <TipoOperacionID>D</TipoOperacionID>\n" + 
    			"            <ActFechaUltima>2014/04/24</ActFechaUltima>\n" + 
    			"            <ActHoraUltima>102001</ActHoraUltima>\n" + 
    			"            <Virtual>N</Virtual>\n" + 
    			"            <FleteACargo></FleteACargo>\n" + 
    			"            <TranspTipoID>10</TranspTipoID>\n" + 
    			"            <TranspMedioID>04</TranspMedioID>\n" + 
    			"            <PesoOrigenDestino>ORIG</PesoOrigenDestino>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroOrdenCargaDesc>0000000000</NroOrdenCargaDesc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NumeroST></NumeroST>\n" + 
    			"            <CupoNegocioEst>S</CupoNegocioEst>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EstadoDsc></EstadoDsc>\n" + 
    			"            <CupoFechaCreacion>2014/04/24</CupoFechaCreacion>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <CupoHoraCreacion>102001</CupoHoraCreacion>\n" + 
    			"            <UsuarioCreacion>extacuevas</UsuarioCreacion>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <OrigenPlantaID>1400</OrigenPlantaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <OrigenAlmacenID>FD01</OrigenAlmacenID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <DestinoPlantaID></DestinoPlantaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <DestinoAlmacenID></DestinoAlmacenID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ClienteProveedor>C</ClienteProveedor>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadID>0000313363</EntidadID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadCUIT>30685146335</EntidadCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <GrupoCUIT>80</GrupoCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <IdentificadorExt></IdentificadorExt>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadRazsoc>DESAB S.A.</EntidadRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadDir>RUTA 33-KM 532</EntidadDir>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadLocalidadCP>6100</EntidadLocalidadCP>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadLocalidadDsc>RUFINO</EntidadLocalidadDsc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadProvinciaID>12</EntidadProvinciaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadPaisID>AR</EntidadPaisID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadCndIvaID>01</EntidadCndIvaID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <EntidadIIBB>9021743865</EntidadIIBB>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PatenteChasis></PatenteChasis>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PatenteAcoplado></PatenteAcoplado>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroOperativo></NroOperativo>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <NroVagon>0000000000</NroVagon>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspCUIT></TranspCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspRazsoc></TranspRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspFactCUIT></TranspFactCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <TranspFactRazsoc></TranspFactRazsoc>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ChoferCUILCUIT></ChoferCUILCUIT>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <ChoferApellidoNom></ChoferApellidoNom>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <CampoID>01</CampoID>\n" + 
    			"            <!--Optional:-->\n" + 
    			"            <PuestoID>01</PuestoID>\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			\n" + 
    			"            <CupoInterlocutor>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <InterlocutorCodFunc></InterlocutorCodFunc>\n" + 
    			"               <InterlocutorCod></InterlocutorCod>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodPostal>6100</CodPostal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <SubCodPostal>0</SubCodPostal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <LocalidadDsc>RUFINO</LocalidadDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Direccion>RUFINO</Direccion>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProvinciaID>12</ProvinciaID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PaisID>AR</PaisID>\n" + 
    			"            </CupoInterlocutor>\n" + 
    			"			\n" + 
    			"            <!--1 or more repetitions:-->\n" + 
    			"			\n" + 
    			"            <CupoDetalles>\n" + 
    			"               <PosicionCupo>01</PosicionCupo>\n" + 
    			"               <ProductoID>FLM9SX28GR0000</ProductoID>\n" + 
    			"               <ClaseVal>FABRI</ClaseVal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <NroCuentaVtaComp>140400000238</NroCuentaVtaComp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrNroRenglon>000001</ContrNroRenglon>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrFechaCreacion>2014/04/24</ContrFechaCreacion>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrCantTotal>1600,000</ContrCantTotal>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ContrCantTotalKG>1600000,000</ContrCantTotalKG>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <CantidadDesp>35,000</CantidadDesp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <KilosDesp>35000,000</KilosDesp>\n" + 
    			"               <PorcTolMas></PorcTolMas>\n" + 
    			"               <PorcTolMenos></PorcTolMenos>\n" + 
    			"               <ClaseDoc>V</ClaseDoc>\n" + 
    			"               <ViaPago></ViaPago>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <NroOrdenComp></NroOrdenComp>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoPrecio>2135,17</ProductoPrecio>\n" + 
    			"               <!--Zero or more repetitions:-->\n" + 
    			"               \n" + 
    			"               <DetCompProductos>\n" + 
    			"                  <VersionFab>0150</VersionFab>\n" + 
    			"                  <PosicionForm></PosicionForm>\n" + 
    			"                  <CodMaterialComp>FLP9TSA0GR0000</CodMaterialComp>\n" + 
    			"                  <ClaseVal>LOCAL</ClaseVal>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <Cantidad>0000000000007000</Cantidad>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <UM>TO</UM>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <CantidadKG>000000000007000000</CantidadKG>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <PorcParticipacion>02000</PorcParticipacion>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <AdmSustitutos>S</AdmSustitutos>\n" + 
    			"                  <!--Zero or more repetitions:-->\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>FLP9TSA0GR0000</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>01</Prioridad>\n" + 
    			"                     <ClaseVal>IMPOR</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>FLP9TSA0GR0000</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>02</Prioridad>\n" + 
    			"                     <ClaseVal>FABRI</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"               </DetCompProductos>\n" + 
    			"               \n" + 
    			"               <DetCompProductos>\n" + 
    			"                  <VersionFab>0150</VersionFab>\n" + 
    			"                  <PosicionForm></PosicionForm>\n" + 
    			"                  <CodMaterialComp>FLP9UAN0GR0000</CodMaterialComp>\n" + 
    			"                  <ClaseVal>FABRI</ClaseVal>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <Cantidad>0000000000028000</Cantidad>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <UM>TO</UM>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <CantidadKG>000000000028000000</CantidadKG>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <PorcParticipacion>08000</PorcParticipacion>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <AdmSustitutos>S</AdmSustitutos>\n" + 
    			"                  <!--Zero or more repetitions:-->\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>FLP9UAN0GR0000</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>01</Prioridad>\n" + 
    			"                     <ClaseVal>IMPOR</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"               </DetCompProductos>\n" + 
    			"               \n" + 
    			"               <DetCompProductos>\n" + 
    			"                  <VersionFab>0150</VersionFab>\n" + 
    			"                  <PosicionForm></PosicionForm>\n" + 
    			"                  <CodMaterialComp>FLP9TSA0GR0000</CodMaterialComp>\n" + 
    			"                  <ClaseVal>LOCAL</ClaseVal>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <Cantidad>0000000000003885</Cantidad>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <UM>TO</UM>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <CantidadKG>000000000003885000</CantidadKG>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <PorcParticipacion>01110</PorcParticipacion>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <AdmSustitutos>S</AdmSustitutos>\n" + 
    			"                  <!--Zero or more repetitions:-->\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>FLP9TSA0GR0000</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>01</Prioridad>\n" + 
    			"                     <ClaseVal>IMPOR</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust>FLP9TSA0GR0000</CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad>02</Prioridad>\n" + 
    			"                     <ClaseVal>FABRI</ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"               </DetCompProductos>\n" + 
    			"               \n" + 
    			"               <DetCompProductos>\n" + 
    			"                  <VersionFab>0150</VersionFab>\n" + 
    			"                  <PosicionForm></PosicionForm>\n" + 
    			"                  <CodMaterialComp>FLM9SX30GR0000</CodMaterialComp>\n" + 
    			"                  <ClaseVal>FABRI</ClaseVal>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <Cantidad>0000000000031115</Cantidad>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <UM>TO</UM>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <CantidadKG>000000000031115000</CantidadKG>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <PorcParticipacion>08890</PorcParticipacion>\n" + 
    			"                  <!--Optional:-->\n" + 
    			"                  <AdmSustitutos>S</AdmSustitutos>\n" + 
    			"                  <!--Zero or more repetitions:-->\n" + 
    			"                  \n" + 
    			"                  <DetCompSustitutos>\n" + 
    			"                     <CodMaterialSust></CodMaterialSust>\n" + 
    			"                     <!--Optional:-->\n" + 
    			"                     <Prioridad></Prioridad>\n" + 
    			"                     <ClaseVal></ClaseVal>\n" + 
    			"                  </DetCompSustitutos>\n" + 
    			"                  \n" + 
    			"               </DetCompProductos>\n" + 
    			"               \n" + 
    			"            </CupoDetalles>\n" + 
    			"			\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"            \n" + 
    			"			<CupoBloqueo>\n" + 
    			"               <BloqueoTipo></BloqueoTipo>\n" + 
    			"               <!--1 or more repetitions:-->\n" + 
    			"               <BloqueoDsc></BloqueoDsc>\n" + 
    			"            </CupoBloqueo>\n" + 
    			"			\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLP9UAN0GR0000</ProductoID>\n" + 
    			"               <ClaseVal>IMPOR</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLUAN</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Pur</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>32,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310280</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLP9TSA0GR0000</ProductoID>\n" + 
    			"               <ClaseVal>LOCAL</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLPLUS 12-0-0-26S</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Pur</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>12,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310520</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLP9UAN0GR0000</ProductoID>\n" + 
    			"               <ClaseVal>FABRI</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLUAN</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Pur</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>32,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310280</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLM9SX30GR0000</ProductoID>\n" + 
    			"               <ClaseVal>FABRI</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLMIX 30-0-0-2,6 SOLMIX</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Mez</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>30,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310520</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLM9SX28GR0000</ProductoID>\n" + 
    			"               <ClaseVal>FABRI</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLMIX 28-0-0-5,2S    BUNGE</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Mez</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>28,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310520</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLP9TSA0GR0000</ProductoID>\n" + 
    			"               <ClaseVal>IMPOR</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLPLUS 12-0-0-26S</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Pur</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>12,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310520</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <CupoTablaProductos>\n" + 
    			"               <ProductoID>FLP9TSA0GR0000</ProductoID>\n" + 
    			"               <ClaseVal>FABRI</ClaseVal>\n" + 
    			"               <ProductoDsc>SOLPLUS 12-0-0-26S</ProductoDsc>\n" + 
    			"               <ProductoCodTipo>Fer</ProductoCodTipo>\n" + 
    			"               <ProductoUM>TO</ProductoUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ProductoEstado>Liq</ProductoEstado>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PuroMezcla>Pur</PuroMezcla>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <MaterialPeligroso>N</MaterialPeligroso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Stokeable></Stokeable>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nitrogeno>12,000</Nitrogeno>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Fosforo>0,000</Fosforo>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Potasio>0,000</Potasio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Azufre></Azufre>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Calcio></Calcio>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Magnesio>0,000</Magnesio>\n" + 
    			"               <ReqPrecinto>SI</ReqPrecinto>\n" + 
    			"               <ReqTapas>Si</ReqTapas>\n" + 
    			"               <ReqDensidad>Despacho</ReqDensidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <ImpCartillaSeg>Si</ImpCartillaSeg>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CheckList>No</CheckList>\n" + 
    			"               <ControlSat>No</ControlSat>\n" + 
    			"               <RelRUCCA>No</RelRUCCA>\n" + 
    			"               <CodUnicoNom>310520</CodUnicoNom>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodLeyenda>3</CodLeyenda>\n" + 
    			"               <MermaCalidad>No</MermaCalidad>\n" + 
    			"               <ConsCapPlanta>Si</ConsCapPlanta>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <CodProdARBA></CodProdARBA>\n" + 
    			"               <EsEnvase>N</EsEnvase>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"            </CupoTablaProductos>\n" + 
    			"			\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			\n" + 
    			"            <CupoPresentaciones>\n" + 
    			"               <PresentacionID>GR000</PresentacionID>\n" + 
    			"               <PresentacionDsc>GRANEL</PresentacionDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoID>GRL0</BultoID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <Nivel>00</Nivel>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoCantidad>0</BultoCantidad>\n" + 
    			"            </CupoPresentaciones>\n" + 
    			"			\n" + 
    			"            <!--Zero or more repetitions:-->\n" + 
    			"			\n" + 
    			"            <CupoBultos>\n" + 
    			"               <BultoID>GRL0</BultoID>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <BultoDsc>GRANEL</BultoDsc>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvaseCapacidad>0,000</EnvaseCapacidad>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvaseUM></EnvaseUM>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvasePeso>0,000</EnvasePeso>\n" + 
    			"               <!--Optional:-->\n" + 
    			"               <EnvasePesoUM></EnvasePesoUM>\n" + 
    			"            </CupoBultos>\n" + 
    			"			\n" + 
    			"         </CupoCabecera>\n" + 
    			"      </urn:CupoNegocio>\n" + 
    			"";

    	ClientXmlRequest req = new ClientXmlRequest(template);
    	
    	req.compile(context);
    	
    	String result = req.getRequest();
    	
    	assertEquals(expected, result);
    	
    }
}
