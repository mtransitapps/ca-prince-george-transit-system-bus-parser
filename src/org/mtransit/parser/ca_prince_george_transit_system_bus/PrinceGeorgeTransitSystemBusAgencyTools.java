package org.mtransit.parser.ca_prince_george_transit_system_bus;

import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// http://bctransit.com/*/footer/open-data
// http://bctransit.com/servlet/bctransit/data/GTFS.zip
// http://bct2.baremetal.com:8080/GoogleTransit/BCTransit/google_transit.zip
public class PrinceGeorgeTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-prince-george-transit-system-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new PrinceGeorgeTransitSystemBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("Generating Prince George Transit System bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating Prince George Transit System bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	private static final String INCLUDE_AGENCY_ID = "9"; // Prince George Transit System only

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (!INCLUDE_AGENCY_ID.equals(gRoute.agency_id)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(GRoute gRoute) {
		return Long.parseLong(gRoute.route_short_name); // use route short name as route ID
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = MSpec.CLEAN_SLASHES.matcher(routeLongName).replaceAll(MSpec.CLEAN_SLASHES_REPLACEMENT);
		routeLongName = MSpec.cleanNumbers(routeLongName);
		routeLongName = MSpec.cleanStreetTypes(routeLongName);
		return MSpec.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_004B8D = "004B8D";
	private static final String COLOR_F8931E = "F8931E";
	private static final String COLOR_8CC63F = "8CC63F";
	private static final String COLOR_00AEEF = "00AEEF";
	private static final String COLOR_49176D = "49176D";
	private static final String COLOR_EC1D8D = "EC1D8D";
	private static final String COLOR_B3AA7E = "B3AA7E";
	private static final String COLOR_00AA4F = "00AA4F";
	private static final String COLOR_FFC10E = "FFC10E";
	private static final String COLOR_0073AE = "0073AE";
	private static final String COLOR_BF83B9 = "BF83B9";
	private static final String COLOR_367D0F = "367D0F";
	private static final String COLOR_8D0B3A = "8D0B3A";
	private static final String COLOR_B5BB19 = "B5BB19";
	private static final String COLOR_00B9BF = "00B9BF";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.route_color)) {
			int rsn = Integer.parseInt(gRoute.route_short_name);
			switch (rsn) {
			// @formatter:off
			case 1: return COLOR_004B8D;
			case 5: return COLOR_F8931E;
			case 11: return COLOR_8CC63F;
			case 12: return COLOR_49176D;
			case 15: return COLOR_EC1D8D;
			case 16: return COLOR_00B9BF;
			case 17: return COLOR_B3AA7E;
			case 18: return COLOR_B3AA7E;
			case 46: return COLOR_8D0B3A;
			case 47: return COLOR_00AA4F;
			case 55: return COLOR_00AEEF;
			case 88: return COLOR_FFC10E;
			case 89: return COLOR_0073AE;
			case 91: return COLOR_BF83B9;
			case 96: return COLOR_B5BB19;
			case 97: return COLOR_367D0F;
			// @formatter:on
			default:
				System.out.println("Unexpected route color " + gRoute);
				System.exit(-1);
				return null;
			}
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), gTrip.direction_id);
	}

	private static final String EXCH = "Exch";
	private static final Pattern EXCHANGE = Pattern.compile("((^|\\W){1}(exchange)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String EXCHANGE_REPLACEMENT = "$2" + EXCH + "$4";

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^[\\d]+[\\S]*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_VIA = Pattern.compile("( via .*$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^.* to )", Pattern.CASE_INSENSITIVE);

	private static final Pattern AND = Pattern.compile("( and )", Pattern.CASE_INSENSITIVE);
	private static final String AND_REPLACEMENT = " & ";

	private static final Pattern CLEAN_P1 = Pattern.compile("[\\s]*\\([\\s]*");
	private static final String CLEAN_P1_REPLACEMENT = " (";
	private static final Pattern CLEAN_P2 = Pattern.compile("[\\s]*\\)[\\s]*");
	private static final String CLEAN_P2_REPLACEMENT = ") ";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = EXCHANGE.matcher(tripHeadsign).replaceAll(EXCHANGE_REPLACEMENT);
		tripHeadsign = ENDS_WITH_VIA.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = STARTS_WITH_TO.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = AND.matcher(tripHeadsign).replaceAll(AND_REPLACEMENT);
		tripHeadsign = CLEAN_P1.matcher(tripHeadsign).replaceAll(CLEAN_P1_REPLACEMENT);
		tripHeadsign = CLEAN_P2.matcher(tripHeadsign).replaceAll(CLEAN_P2_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = MSpec.cleanStreetTypes(tripHeadsign);
		tripHeadsign = MSpec.cleanNumbers(tripHeadsign);
		return MSpec.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_BOUND = Pattern.compile("(^(east|west|north|south)bound)", Pattern.CASE_INSENSITIVE);

	private static final Pattern AT = Pattern.compile("( at )", Pattern.CASE_INSENSITIVE);
	private static final String AT_REPLACEMENT = " / ";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STARTS_WITH_BOUND.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = AT.matcher(gStopName).replaceAll(AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = MSpec.cleanStreetTypes(gStopName);
		gStopName = MSpec.cleanNumbers(gStopName);
		return MSpec.cleanLabel(gStopName);
	}
}
